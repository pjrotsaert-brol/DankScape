#include "urldownloader.h"
#include <QUrl>
#include <QNetworkRequest>
#include <QNetworkReply>
#include <iostream>
#include <fstream>
#include "Widgets/mainwindow.h"

#define DL_FILE   0
#define DL_STRING 1

typedef void (*DLFileCallback)(void*, bool);
typedef void (*DLStringCallback)(void*, QString, bool);

URLDownloader* URLDownloader::singleton = nullptr;

URLDownloader::URLDownloader() : QObject(), DebugWriter("File Downloader")
{
    manager = new QNetworkAccessManager;
    QObject::connect(manager, SIGNAL(finished(QNetworkReply*)),this, SLOT(DownloadFinished(QNetworkReply*)));
}

URLDownloader::~URLDownloader()
{

    delete manager;
}

void URLDownloader::DownloadFile(QString url, QString filename, void (*callbackFunc)(void*, bool), void* cbData, bool _silent)
{
    silent = _silent;
    targetUrl = url;

    if(!silent)
        dbg << "Downloading: '" << targetUrl << "'..." << endl;

    QNetworkRequest *request = new QNetworkRequest(QUrl(url));
    QNetworkReply* reply = manager->get(*request);
    reply->setUserData(0, (QObjectUserData*)callbackFunc);
    reply->setUserData(1, (QObjectUserData*)cbData);
    reply->setUserData(2, (QObjectUserData*)DL_FILE);
    reply->setUserData(3, (QObjectUserData*)(new QString(filename)));

    QObject::connect(reply, SIGNAL(downloadProgress(qint64,qint64)), this, SLOT(DownloadProgress(qint64,qint64)));
}

void URLDownloader::DownloadString(QString url, void (*callbackFunc)(void*, QString, bool), void *cbData, bool _silent)
{
    silent = _silent;
    targetUrl = url;

    if(!silent)
        dbg << "Downloading: '" << targetUrl << "'..." << endl;

    QNetworkRequest *request = new QNetworkRequest(QUrl(url));
    QNetworkReply* reply = manager->get(*request);
    reply->setUserData(0, (QObjectUserData*)callbackFunc);
    reply->setUserData(1, (QObjectUserData*)cbData);
    reply->setUserData(2, (QObjectUserData*)DL_STRING);

    QObject::connect(reply, SIGNAL(downloadProgress(qint64,qint64)), this, SLOT(DownloadProgress(qint64,qint64)));
}


void URLDownloader::DownloadFinished(QNetworkReply *reply)
{
    QObject::disconnect(reply, SIGNAL(downloadProgress(qint64,qint64)), this, SLOT(DownloadProgress(qint64,qint64)));

    if(!silent)
        dbg << "Downloading: '" << targetUrl << "'... (100%)  => Done!" << repl;

    if(reply->isReadable())
    {
        QByteArray data = reply->readAll();
        if((unsigned long long)(reply->userData(2)) == DL_FILE)
        {
            bool success = true;
            std::ofstream out(((QString*)reply->userData(3))->toStdString(), std::ios::binary);
            if(out.is_open())
                out.write(data.constData(), (size_t)data.size());
            else
                success = false;
            out.close();
            delete ((QString*)reply->userData(3));

            DLFileCallback callbackFunc = (DLFileCallback)reply->userData(0);
            callbackFunc(reply->userData(1), success);
        }
        else
        {
            DLStringCallback callbackFunc = (DLStringCallback)reply->userData(0);
            callbackFunc(reply->userData(1), data.toStdString().c_str(), true);
        }
    }
    else
    {
        if((unsigned long long)(reply->userData(2)) == DL_FILE)
        {
            DLFileCallback callbackFunc = (DLFileCallback)reply->userData(0);
            callbackFunc(reply->userData(1), false);
        }
        else
        {
            DLStringCallback callbackFunc = (DLStringCallback)reply->userData(0);
            callbackFunc(reply->userData(1), "", false);
        }
    }


    //reply->deleteLater();
}

void URLDownloader::DownloadProgress(qint64 received, qint64 total)
{
    if(!silent)
        dbg << "Downloading: '" << GetSingleton()->targetUrl << "'... (" << QString::number(double(received)* 100.0 / double(total)) + "%)" << repl;
}
