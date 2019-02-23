#ifndef JMANAGER_H
#define JMANAGER_H

#include <jni.h>
#include <map>
#include <jawt.h>
#include <jawt_md.h>

#include "debugwriter.h"
#include "dankscape.h"
#include <unordered_map>

#define DANKSCAPE_APPLETLOADER_CLASS    "dankscape/loader/AppletLoader"
#define DANKSCAPE_NATIVEINTERFACE_CLASS "dankscape/nativeinterface/NativeInterface"

class JManager : public DebugWriter
{
    static JManager* singleton;

    JNIEnv *env = NULL;
    JavaVM* jvm = NULL;

    jclass cAppletLoader = NULL;
    jclass cNativeInterface = NULL;

    jobject oAppletLoader = NULL;

    jmethodID fInit = NULL;
    jmethodID fOnResize = NULL;
    jmethodID fGetRemoteHash = NULL;
    jmethodID fGetLocalHash = NULL;
    jmethodID fGetWindowHandle = NULL;

public:
    JManager();

    static JManager* GetSingleton();

    bool Init();

    bool CheckForUpdate(std::string jarUrl);
    HANDLE StartApplet(std::string jarName, std::string codeName, std::map<std::string, std::string> params, int appletW, int appletH);
    void ResizeApplet(int w, int h);

    int GetJarFileHash(std::string filename);
    int GetJarURLHash(std::string url);

    JNIEnv* GetJNI();

    static bool CheckNulls(int c, void** ptrs);

    std::string GetJString(jstring str);
    void CatchExceptions();

    void LoadHooks(std::unordered_map<std::string, ClassHook*> hooks);
};

#define JStringToString JManager::GetSingleton()->GetJString

#endif // JMANAGER_H
