#ifndef DANKSCAPE_H
#define DANKSCAPE_H

#include <string>
#include "debugwriter.h"
#include <unordered_map>
#include <map>

struct FieldHook
{
    std::string name, internalName, type;
    int multiplier = 0;
    bool isStatic = false;
};

struct ClassHook
{
    std::string name, internalName;
    std::unordered_map<std::string, FieldHook*> fields;
};

class DankScape : public DebugWriter
{
    static DankScape* singleton;

    QString rsUrl       = "http://oldschool1.runescape.com/";
    QString hookUrl     = "https://raw.githubusercontent.com/zelfrax/dummy/master/dummyfile.json";
    //QString itemListUrl = "https://rsbuddy.com/exchange/summary.json";

    std::string jarUrl, jarClassName;
    std::map<std::string, std::string> parameters;

    std::unordered_map<std::string, ClassHook*> classHooks;

    static void OnPageSourceDownloadedS(void* ud, QString pageSource, bool success);
    static void OnHooksDownloadedS(void* ud, QString pageSource, bool success);
    static void OnGamePackDownloadedS(void* ud, bool success);

    void OnPageSourceDownloaded(QString pageSource, bool success);
    void OnUpdateFinished(bool success);

    void Start();

public:
    DankScape();


    void Init();
    bool CheckForUpdate(std::string jarUrl);

    std::unordered_map<std::string, ClassHook*> GetHooks();

    static DankScape* GetSingleton();

};

#endif // DANKSCAPE_H
