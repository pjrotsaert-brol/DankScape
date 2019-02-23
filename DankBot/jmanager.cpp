#include "jmanager.h"
#include <QString>
#include "Widgets/mainwindow.h"
#include <map>
#include <qfile.h>
#include <iostream>
#include <qmessagebox.h>
#include <jvmti.h>

#define APPLETLOADER_JAR "dankscape.jar"

bool RegisterJavaBindings(JNIEnv* env);

JManager* JManager::singleton = NULL;

JManager::JManager() : DebugWriter("JVM Manager")
{

}

JManager* JManager::GetSingleton()
{
    if(!singleton)
        singleton = new JManager;
    return singleton;
}

void JNICALL y_exit(jint code)
{
    printf("CALLED y_exit\n");

    QMessageBox::information(MainWindow::GetSingleton(), "FATAL ERROR: The JVM has shut down unexpectedly!", "DankScape - Fatal Error");
    exit(code);
}

void JNICALL y_abort()
{
    printf("CALLED y_abort\n");

}

jint JNICALL y_fprintf(FILE *fp, const char *format, va_list args)
{
    printf(format, args);
    std::cout << "LOOOOL" << std::endl;

    /*
    jint rc;
    if (fp == NULL)
        if ((fp = fopen("vm.out", "w")) == NULL) return -1;
    rc = vfprintf(fp, format, args);
    fflush(fp);*/

    return 0;
}

bool JManager::Init()
{
    dbg << "Starting Java VM..." << endl;

    JavaVMInitArgs vmArgs;
    JavaVMOption options[8];

    std::string classPathOpt = QString(QString("-Djava.class.path=") + QString(APPLETLOADER_JAR)).toStdString();

    options[0].optionString = (char*)classPathOpt.c_str();
    options[1].optionString = (char*)"-Xmx512M";
    options[2].optionString = (char*)"-Xbootclasspath/p:dankscape.jar";
    options[3].optionString = (char*)"-verbose:jni,class,gc";

    options[4].optionString = (char*)"abort";
    options[4].extraInfo = (void*)y_abort;

    options[5].optionString = (char*)"exit";
    options[5].extraInfo = (void*)y_exit;

    options[6].optionString = (char*)"vfprintf";
    options[6].extraInfo = (void*)y_fprintf;

    options[7].optionString = (char*)"-agentpath:DankAgent.dll";

    vmArgs.nOptions = 6;
    vmArgs.options = options;
    vmArgs.version = JNI_VERSION_1_8;
    vmArgs.ignoreUnrecognized = true;

    JNI_GetDefaultJavaVMInitArgs(&vmArgs);

    int errCode = JNI_CreateJavaVM(&jvm, (void**)&env, &vmArgs);
    if (errCode < 0)
    {
        dbg << "Error creating Java VM! (Error code: " << errCode << ")" << endl;
        return false;
    }

    dbg << "Java VM created successfully!" << endl;

    cAppletLoader = env->FindClass(DANKSCAPE_APPLETLOADER_CLASS);
    if(!cAppletLoader)
    {
        dbg << "ERROR: Could not find class '" << DANKSCAPE_APPLETLOADER_CLASS << "'!" << endl;
        return false;
    }

    cNativeInterface = env->FindClass(DANKSCAPE_NATIVEINTERFACE_CLASS);
    if(!cNativeInterface)
    {
        dbg << "ERROR: Could not find class '" << DANKSCAPE_NATIVEINTERFACE_CLASS << "'!" << endl;
        return false;
    }

    std::string initSig = "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;III)";
    initSig += std::string("L") + std::string(DANKSCAPE_APPLETLOADER_CLASS) + ";";

    fInit = env->GetStaticMethodID(cAppletLoader, "onCreate", initSig.c_str());
    if(!fInit)
    {
        dbg << "ERROR: Could not find 'onCreate' method in AppletLoader class!" << endl;
        return false;
    }

    fOnResize     = env->GetMethodID(cAppletLoader, "onResize", "(II)V");
    if(!fOnResize)
        dbg << "WARNING: Resize method 'onResize' not found!" << endl;

    fGetLocalHash  = env->GetStaticMethodID(cNativeInterface, "getLocalHash", "(Ljava/lang/String;)I");
    fGetRemoteHash = env->GetStaticMethodID(cNativeInterface, "getRemoteHash", "(Ljava/lang/String;)I");

    fGetWindowHandle = env->GetMethodID(cAppletLoader, "getWindowHandle", "()J");
    if(!fGetWindowHandle)
    {
        dbg << "ERROR: Could not find 'getWindowHandle' method in AppletLoader class!" << endl;
        return false;
    }

    return RegisterJavaBindings(env);
}


int JManager::GetJarFileHash(std::string filename)
{
    if(cNativeInterface && fGetLocalHash)
    {
        int i = env->CallStaticIntMethod(cNativeInterface, fGetLocalHash, env->NewStringUTF(filename.c_str()));
        CatchExceptions();
        return i;
    }
    return -1;
}

int JManager::GetJarURLHash(std::string url)
{
    if(cNativeInterface && fGetRemoteHash)
    {
        int i = env->CallStaticIntMethod(cNativeInterface, fGetRemoteHash, env->NewStringUTF(url.c_str()));
        CatchExceptions();
        return i;
    }
    return -1;
}

std::string JManager::GetJString(jstring str)
{
    if(!str)
        return "";

    const char* cstr = env->GetStringUTFChars(str, NULL);
    std::string result = cstr;
    env->ReleaseStringUTFChars(str, cstr);
    return result;
}

void JManager::CatchExceptions()
{
    if(!env->ExceptionCheck())
        return;

    jthrowable ex = env->ExceptionOccurred();
    if(ex)
    {
        std::string exText = "An unknown exception occurred!";

        jclass clazz = env->GetObjectClass(ex);

        if(clazz)
        {
            std::string message = "No message available.", stackTrace = "Stack trace unavailable.", className = "Exception";

            jmethodID mGetStackTrace = env->GetMethodID(clazz, "getStackTrace", "()[Ljava/lang/StackTraceElement;");
            jmethodID mGetClassName  = env->GetMethodID(env->FindClass("java/lang/Class"), "getName", "()Ljava/lang/String;");
            jmethodID mGetMessage    = env->GetMethodID(clazz, "getMessage", "()Ljava/lang/String;");

            if(mGetClassName)
            {
                jstring jclassName = (jstring)env->CallObjectMethod(clazz, mGetClassName);
                if(jclassName)
                    className = GetJString(jclassName);
            }
            if(mGetMessage)
            {
                jstring jMessage   = (jstring)env->CallObjectMethod(ex, mGetMessage);
                if(jMessage)
                    message = GetJString(jMessage);
            }
            if(mGetStackTrace)
            {
                jmethodID mGetClassName  = env->GetMethodID(env->FindClass("java/lang/StackTraceElement"), "getClassName", "()Ljava/lang/String;");
                jmethodID mGetMethodName = env->GetMethodID(env->FindClass("java/lang/StackTraceElement"), "getMethodName", "()Ljava/lang/String;");
                jmethodID mGetFileName   = env->GetMethodID(env->FindClass("java/lang/StackTraceElement"), "getFileName", "()Ljava/lang/String;");
                jmethodID mGetLineNumber = env->GetMethodID(env->FindClass("java/lang/StackTraceElement"), "getLineNumber", "()I");

                if(mGetClassName && mGetMethodName && mGetFileName && mGetLineNumber)
                {
                    stackTrace = "";
                    jobjectArray stackTraceArr = (jobjectArray)env->CallObjectMethod(ex, mGetStackTrace);
                    int len = env->GetArrayLength(stackTraceArr);
                    for(int i = 0;i < len;i++)
                    {
                        jstring stClassName  = (jstring)env->CallObjectMethod(env->GetObjectArrayElement(stackTraceArr, i), mGetClassName);
                        jstring stMethodName = (jstring)env->CallObjectMethod(env->GetObjectArrayElement(stackTraceArr, i), mGetMethodName);
                        jstring stFileName   = (jstring)env->CallObjectMethod(env->GetObjectArrayElement(stackTraceArr, i), mGetFileName);
                        jint    stLineNumber = env->CallIntMethod(env->GetObjectArrayElement(stackTraceArr, i), mGetLineNumber);

                        stackTrace += "    at " + GetJString(stClassName) + "." + GetJString(stMethodName) +
                                " (" + GetJString(stFileName) + ":" + std::to_string(stLineNumber) + ")\n";
                    }
                }
            }

            exText = "UNCAUGHT EXCEPTION: " + className + ": " + message + (stackTrace != "" ? "\n" : "") + stackTrace;
        }

        env->ExceptionClear();

        dbg << exText << endl;
    }
}

HANDLE JManager::StartApplet(std::string jarName, std::string codeName, std::map<std::string, std::string> params, int appletW, int appletH)
{
    dbg << "Starting AppletLoader..." << endl;

    jobjectArray keys   = env->NewObjectArray(params.size(), env->FindClass("java/lang/String"), env->NewStringUTF(""));
    jobjectArray values = env->NewObjectArray(params.size(), env->FindClass("java/lang/String"), env->NewStringUTF(""));
    int i = 0;
    for(auto p : params)
    {
        env->SetObjectArrayElement(keys, i, env->NewStringUTF(p.first.c_str()));
        env->SetObjectArrayElement(values, i, env->NewStringUTF(p.second.c_str()));
        i++;
    }

    oAppletLoader = env->CallStaticObjectMethod(cAppletLoader, fInit,
                                                env->NewStringUTF(jarName.c_str()),
                                                env->NewStringUTF(codeName.c_str()),
                                                keys, values, appletW, appletH, (jint)MainWindow::GetSingleton()->winId());

    CatchExceptions();

    if(!oAppletLoader)
    {
        dbg << "ERROR: Failed to get the AppletLoader singleton!" << endl;
        return 0;
    }

    MainWindow::GetSingleton()->winId();

    ResizeApplet(appletW, appletH);

    HANDLE h = (HANDLE)env->CallLongMethod(oAppletLoader, fGetWindowHandle);
    CatchExceptions();
    return h;
}

bool JManager::CheckNulls(int c, void** ptrs)
{
   int i = 0;
   while(ptrs[i])
   {
       i++;
       if(i >= c)
           return true;
   }
   std::cout << "CheckNulls(): Field " << i << " was NULL." << std::endl;
   return false;
}

void JManager::LoadHooks(std::unordered_map<std::string, ClassHook*> hooks)
{
    //dbg << "Loading hooks into the JVM... " << endl;
    jmethodID mLoadHooks = env->GetMethodID(cAppletLoader, "loadHooks", "(Ljava/util/HashMap;)V");

    jclass hm = env->FindClass("java/util/HashMap");
    jmethodID hmInit = env->GetMethodID(hm, "<init>", "()V");
    jmethodID hmPut  = env->GetMethodID(hm, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    jclass cClassHook = env->FindClass("dankscape/misc/ClassHook");
    jclass cFieldHook = env->FindClass("dankscape/misc/FieldHook");

    jmethodID mInitClassHook = env->GetMethodID(cClassHook, "<init>", "(Ljava/lang/String;Ljava/lang/String;[Ldankscape/misc/FieldHook;)V");
    jmethodID mInitFieldHook = env->GetMethodID(cFieldHook, "<init>", "(Ljava/lang/String;Ljava/lang/String;I)V");
    jmethodID mInitFieldHookEmpty = env->GetMethodID(cFieldHook, "<init>", "()V");

    void* toCheck[] = { (void*)hm, (void*)hmInit, (void*)hmPut, (void*)cClassHook,
                        (void*)cFieldHook, (void*)mInitClassHook, (void*)mInitFieldHook, (void*)mInitFieldHookEmpty };

    if(!CheckNulls(8, toCheck))
    {
        QMessageBox::information(MainWindow::GetSingleton(), "DankScape", "ERROR: Could not pass hooks to AppletLoader!\n"
                                                                          "One or more methods and/or fields were not found!");
        return;
    }

    jobject fieldProto = env->NewObject(cFieldHook, mInitFieldHookEmpty);

    jobject classHooksMap = env->NewObject(hm, hmInit);
    for(auto it : hooks)
    {
        ClassHook* hC = it.second;
        jobjectArray fieldArr = env->NewObjectArray(hC->fields.size(), cFieldHook, fieldProto);
        int idx = 0;
        for(auto it : hC->fields)
        {
            FieldHook* hF = it.second;
            jobject fieldHook = env->NewObject(cFieldHook, mInitFieldHook,
                                           env->NewStringUTF(hF->name.c_str()),
                                           env->NewStringUTF(hF->internalName.c_str()), hF->multiplier);
            env->SetObjectArrayElement(fieldArr, idx++, fieldHook);
        }
        jobject classHook = env->NewObject(cClassHook, mInitClassHook,
                                           env->NewStringUTF(hC->name.c_str()),
                                           env->NewStringUTF(hC->internalName.c_str()), fieldArr);
        env->CallObjectMethod(classHooksMap, hmPut, env->NewStringUTF(hC->name.c_str()), classHook);
    }

    env->CallBooleanMethod(oAppletLoader, mLoadHooks, classHooksMap);
    CatchExceptions();
}

void JManager::ResizeApplet(int w, int h)
{
    if(fOnResize)
    {
        env->CallVoidMethod(oAppletLoader, fOnResize, w, h);
        CatchExceptions();
    }
}

JNIEnv* JManager::GetJNI()
{
    return env;
}

