#include "dankscape.h"
#include <QJsonDocument>
#include <qjsonobject.h>
#include <qjsonarray.h>
#include <qstring.h>
#include <qfile.h>
#include "jmanager.h"
#include "urldownloader.h"
#include "utils.h"
#include "Widgets/mainwindow.h"
#include "Widgets/appletframe.h"
#include <qmessagebox.h>

DankScape* DankScape::singleton = nullptr;

DankScape::DankScape() : DebugWriter("Bot")
{

}

void DankScape::Init()
{
    dbg << "Initializing..." << endl;
    if(JManager::GetSingleton()->Init())
    {
        dbg << "Fetching '" << rsUrl << "'..." << endl;
        URLDownloader::GetSingleton()->DownloadString(rsUrl, DankScape::OnPageSourceDownloadedS, this);
    }
}

bool DankScape::CheckForUpdate(std::string jarUrl)
{
    dbg << "Checking for RS Client updates..." << endl;
    dbg << "Update URL: " << jarUrl << endl;

    QFile localJar("gamepack.jar");
    if(!localJar.exists())
    {
        dbg << "'Gamepack.jar' not found: downloading new one.." << endl;
        return true;
    }

    int localHash  = JManager::GetSingleton()->GetJarFileHash("gamepack.jar");
    int remoteHash = JManager::GetSingleton()->GetJarURLHash(jarUrl);

    dbg << "Local hash: " << localHash << ",  Remote hash: " << remoteHash << endl;
    if(localHash != remoteHash && localHash != -1 && remoteHash != -1)
    {
        dbg << "An update is available." << endl;
        return true;
    }
    else if(localHash == -1 || remoteHash == -1)
    {
        dbg << "ERROR: Could not determine local or remote .jar hash, forcing update..." << endl;
        return true;
    }
    else
        return false;
}

void DankScape::OnPageSourceDownloadedS(void* ud, QString pageSource, bool success)
{
    DankScape* self = (DankScape*)ud;
    self->OnPageSourceDownloaded(pageSource, success);
}

void DankScape::OnHooksDownloadedS(void *ud, QString pageSource, bool success)
{
    DankScape* self = (DankScape*)ud;
    if(!success)
    {
        QMessageBox::information(MainWindow::GetSingleton(), "DankScape", "ERROR: The bot was unable to download the required hooks!");
        return;
    }

    self->dbg << "Parsing hooks..." << self->endl;
    QJsonDocument doc = QJsonDocument::fromJson(pageSource.toUtf8());
    QJsonArray arr = doc.array();

    for(int i = 0;i < arr.size();i++)
    {
        QJsonObject classDef  = arr.at(i).toObject().find("rsClass").value().toObject();
        QJsonArray  fieldsArr = arr.at(i).toObject().find("rsFields").value().toArray();

        ClassHook *hC = new ClassHook;
        hC->name = classDef.find("refactoredName").value().toString().toStdString();
        hC->internalName = classDef.find("obfuscatedName").value().toString().toStdString();

        for(int j = 0;j < fieldsArr.size();j++)
        {
            FieldHook *hF = new FieldHook;
            QJsonObject fieldDef = fieldsArr.at(j).toObject();
            hF->name = fieldDef.find("refactoredName").value().toString().toStdString();
            hF->internalName = fieldDef.find("fieldName").value().toString().toStdString();
            if(fieldDef.find("multiplier") != fieldDef.end())
                hF->multiplier = fieldDef.find("multiplier").value().toInt();

            hC->fields[hF->name] = hF;
        }

        self->classHooks[hC->name] = hC;
    }
    self->dbg << "Parsed hooks for " << self->classHooks.size() << " classes." << self->endl;
    GetSingleton()->Start();
}

void DankScape::OnGamePackDownloadedS(void* ud, bool success)
{
    DankScape* self = (DankScape*)ud;
    self->OnUpdateFinished(success);
}

void DankScape::OnPageSourceDownloaded(QString pageSource, bool success)
{
    if(!success)
    {
        dbg << "An error occurred while trying to fetch the page." << endl;
        return;
    }

    dbg << "Parsing page source..." << endl;
    std::string source = pageSource.toStdString();
    std::string jarName = Utils::ParseValue(source, "'archive=", ".jar") + ".jar";

    jarUrl  = rsUrl.toStdString() + jarName.c_str();
    jarClassName = Utils::ParseValue(source, "'code=", ".class");

    int curParamIdx = 0;
    while(source.find("<param name=\"", curParamIdx) != source.npos)
    {
        std::string key = Utils::ParseValue(source, "<param name=\"", "\"", curParamIdx, &curParamIdx);
        std::string val = Utils::ParseValue(source, "value=\"", "\"", curParamIdx, &curParamIdx);
        parameters[key] = val;
    }

    dbg << "Done, parsed " << parameters.size() << " parameters." << endl;

    if(CheckForUpdate(jarUrl))
        URLDownloader::GetSingleton()->DownloadFile(jarUrl.c_str(), "gamepack.jar", OnGamePackDownloadedS, this);
    else
        Start();
}

void DankScape::OnUpdateFinished(bool success)
{
    if(!success)
    {
        dbg << "An error occurred while downloading the RS Client (gamepack.jar)!" << endl;
        return;
    }

    Start();
}

void DankScape::Start()
{
    if(classHooks.size() <= 0)
    {
        dbg << "Downloading hooks..." << endl;
        URLDownloader::GetSingleton()->DownloadString(hookUrl, DankScape::OnHooksDownloadedS, this);
        return;
    }

    // Start the RS Applet and retrieve its window handle
    HANDLE hwnd = JManager::GetSingleton()->StartApplet("gamepack.jar", jarClassName, parameters,
                                                        MainWindow::GetSingleton()->GetAppletFrame()->width(),
                                                        MainWindow::GetSingleton()->GetAppletFrame()->height());

    if(!hwnd)
        QMessageBox::information(MainWindow::GetSingleton(), "DankScape Error", "Error: The applet failed to start properly!");

    // Embed the applet into the Qt GUI
    MainWindow::GetSingleton()->GetAppletFrame()->EmbedWindow(hwnd);

    // Prepare the hooks on the Java side
    JManager::GetSingleton()->LoadHooks(classHooks);
}

std::unordered_map<std::string, ClassHook*> DankScape::GetHooks()
{
    return classHooks;
}

/************/

DankScape* DankScape::GetSingleton()
{
    if(!singleton)
        singleton = new DankScape;
    return singleton;
}
