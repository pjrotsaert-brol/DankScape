#include <jni.h>
#include <jmanager.h>
#include <Widgets/mainwindow.h>
#include <iostream>
#include <qaction.h>
#include <QActionGroup>
#include <qwidget.h>
#include <QMenuBar>
#include <QMenu>
#include <QToolBar>
#include <qstring.h>
#include <ui_mainwindow.h>
#include <QSignalMapper>
#include "javaactioncallbacklistener.h"
#include <QMessageBox>

int jIdCounter = 1;
std::unordered_map<int, QMenu*>   jMenus;
std::unordered_map<int, QAction*> jActions;
std::unordered_map<int, std::pair<int, std::string>> jCallbacks;

// -------------------- Java Bindings ------------------------- //

JavaActionCallbackListener* JavaActionCallbackListener::singleton = NULL;

JavaActionCallbackListener::JavaActionCallbackListener(QObject* parent) : QObject(parent)
{

    triggeredMapper = new QSignalMapper();
    toggledMapper = new QSignalMapper();

    connect(triggeredMapper, SIGNAL(mapped(int)), this, SLOT(onActionTriggered(int)));
    connect(toggledMapper, SIGNAL(mapped(int)), this, SLOT(onActionToggled(int)));
}
JavaActionCallbackListener::~JavaActionCallbackListener()
{
    delete triggeredMapper;
    delete toggledMapper;
}

JavaActionCallbackListener* JavaActionCallbackListener::GetSingleton()
{
    if(!singleton)
        singleton = new JavaActionCallbackListener();
    return singleton;
}

void JavaActionCallbackListener::BindCallback(QAction* action, int id)
{
    connect(action, SIGNAL(triggered(bool)), triggeredMapper, SLOT(map()));
    connect(action, SIGNAL(toggled(bool)), toggledMapper, SLOT(map()));

    triggeredMapper->setMapping(action, id);
    toggledMapper->setMapping(action, id);
}

void JavaActionCallbackListener::onActionTriggered(int id)
{
    jclass clazz = JManager::GetSingleton()->GetJNI()->FindClass(DANKSCAPE_NATIVEINTERFACE_CLASS);
    if(clazz)
    {
        jmethodID method = JManager::GetSingleton()->GetJNI()->GetStaticMethodID(clazz, jCallbacks[id].second.c_str(), "(I)V");
        JManager::GetSingleton()->CatchExceptions();
        if(method)
            JManager::GetSingleton()->GetJNI()->CallStaticVoidMethod(clazz, method, jCallbacks[id].first);
        JManager::GetSingleton()->CatchExceptions();
    }
}

void JavaActionCallbackListener::onActionToggled(int id)
{
    jclass clazz = JManager::GetSingleton()->GetJNI()->FindClass(DANKSCAPE_NATIVEINTERFACE_CLASS);
    if(clazz)
    {
        jmethodID method = JManager::GetSingleton()->GetJNI()->GetStaticMethodID(clazz, jCallbacks[id].second.c_str(), "(IZ)V");
        JManager::GetSingleton()->CatchExceptions();
        if(method)
            JManager::GetSingleton()->GetJNI()->CallStaticVoidMethod(clazz, method, jCallbacks[id].first, jActions[id]->isChecked());
        JManager::GetSingleton()->CatchExceptions();
    }
}

void Java_Debug_println(JNIEnv* env, jclass clazz, jstring txt, jboolean replaceLastLine)
{
    QString text = QString(JStringToString(txt).c_str());
    GuiOp op(OP_DEBUGPRINT);
    op.replaceLastLine = replaceLastLine;
    op.text = text;
    MainWindow::GetSingleton()->PushOp(op);
}

void Java_Debug_setStatusTextLeft(JNIEnv* env, jclass clazz, jstring s)
{
    QString text = QString(JStringToString(s).c_str());
    GuiOp op(OP_SETSTATUSTEXT);
    op.text = text;
    op.statusTextLocation = STATUSTEXT_LEFT;
    MainWindow::GetSingleton()->PushOp(op);
}

void Java_Debug_setStatusTextRight(JNIEnv* env, jclass clazz, jstring s)
{
    QString text = QString(JStringToString(s).c_str());
    GuiOp op(OP_SETSTATUSTEXT);
    op.text = text;
    op.statusTextLocation = STATUSTEXT_RIGHT;
    MainWindow::GetSingleton()->PushOp(op);
}

void Java_RequestUICallback(JNIEnv* env, jclass clazz, jstring funcName, jint userdata)
{
    GuiOp op = GuiOp([](GuiOp* op) ->
    void {
        jmethodID m = op->env->GetStaticMethodID(op->clazz, op->str.c_str(), "(I)V");
        if(m)
            op->env->CallStaticVoidMethod(op->clazz, m, op->arg);
    }, env, clazz, (int)userdata, JStringToString(funcName));
    MainWindow::GetSingleton()->PushOp(op);
}

jint Java_CreateMenu_SI(JNIEnv* env, jclass clazz, jstring name, jint parentId)
{
    QString title = JStringToString(name).c_str();

    QMenu* menu = NULL;
    for(auto it : jMenus)
    {
        if(it.second->title() == title)
        {
            menu = it.second;
            break;
        }
    }
    if(!menu)
    {
        QMenu* parent = NULL;
        if(jMenus.find(parentId) != jMenus.end())
            parent = jMenus[parentId];
        menu = new QMenu(title);

        menu->setStyleSheet("\nQMenu \n{\nbackground: solid #212e51;\ncolor: white;\nborder: 1px solid #151d33;\n}\n\nQMenu::item\n{\n\nmargin: 0px 0px;\npadding: 4px 10px;\npadding-left: 20px;\npadding-right: 20px;\n}\n\nQMenu::item:selected\n{\nbackground-color: #3a508c;\n}\n\nQMenu::icon:checked {\npadding-right: 5px;\npadding-left: 3px;\n}\n\nQMenuBar::item:pressed {\nbackground-color: #1f2b4d;\n}\n\n QMenuBar {\n	background-color: #151d33;\n	border: 0px solid #151d33;\n	margin: 0px;\n	color: white;\n}\n\nQMenuBar::item {\n\n	margin-bottom: 0px;\n	margin-top: 0px;\n\n	border-top: 2px solid  #151d33;\n	border-bottom: 2px solid #151d33;\n	margin-left: 2px;\n	padding: 1px 5px;\n	background-color: #151d33;\n}\n\nQMenuBar::item:selected {\n	border-color: #1f2b4d;\n	background-color: #1f2b4d;\n	\n	\n}\n\nQMenuBar::item:pressed {\n	background-color:  #2f4173;\n	border-color: #2f4173;\n	border-bottom: 2px solid rgb(255, 255, 255);\n	\n}");

        if(!parent)
        {
            MainWindow::GetSingleton()->GetUI()->menuBar->addMenu(menu);
            if(!MainWindow::GetSingleton()->GetUI()->menuBar->isVisible())
                MainWindow::GetSingleton()->GetUI()->menuBar->setVisible(true);
        }
        else
            parent->addMenu(menu);

        jMenus[jIdCounter] = menu;
    }
    return jIdCounter++;
}

jint Java_CreateMenu_S(JNIEnv* env, jclass clazz, jstring name)
{
    return Java_CreateMenu_SI(env, clazz, name, -1);
}

jint Java_CreateAction(JNIEnv* env, jclass clazz, jstring title, jint parentId, jstring cbFuncName, jint cbId, jboolean checkable, jboolean isChecked)
{
    QMenu* parent = NULL;
    if(jMenus.find(parentId) != jMenus.end())
        parent = jMenus[parentId];

    if(parent)
    {
        QAction* action = new QAction(JStringToString(title).c_str());
        action->setCheckable(checkable);
        action->setChecked(isChecked);
        QList<QAction*> actions;
        actions.append(action);
        parent->addActions(actions);

        jCallbacks[jIdCounter] = std::make_pair(cbId, JStringToString(cbFuncName));
        JavaActionCallbackListener::GetSingleton()->BindCallback(action, jIdCounter);

        jActions[jIdCounter] = action;
        return jIdCounter++;
    }
    return -1;
}

void Java_AddMenuSeparator(JNIEnv* env, jclass clazz, jint parentId)
{
    if(jMenus.find(parentId) != jMenus.end())
        jMenus[parentId]->addSeparator();
}

void Java_AddToolBarSeparator(JNIEnv* env, jclass clazz)
{
    MainWindow::GetSingleton()->GetUI()->toolBar->addSeparator();
}

void Java_AddActionToToolBar(JNIEnv* env, jclass clazz, jint actionId, jint alignment)
{
    QAction* action = NULL;
    if(jActions.find(actionId) != jActions.end())
        action = jActions[actionId];

    if(action)
    {
        QList<QAction*> actions;
        actions.append(action);


        if(alignment == 0)
            MainWindow::GetSingleton()->GetUI()->toolBar->insertAction(MainWindow::GetSingleton()->GetToolBarSpacer(), action);
        else
        {
            QAction* actionAfterSpacer = NULL;
            bool spacerEncountered = false;
            for(QAction* action : MainWindow::GetSingleton()->GetUI()->toolBar->actions())
            {
                if(spacerEncountered)
                {
                    actionAfterSpacer = action;
                    break;
                }
                if(action == MainWindow::GetSingleton()->GetToolBarSpacer())
                    spacerEncountered = true;
            }

            if(actionAfterSpacer)
                MainWindow::GetSingleton()->GetUI()->toolBar->insertAction(actionAfterSpacer, action);
            else
            {
                QList<QAction*> actions;
                actions.append(action);
                MainWindow::GetSingleton()->GetUI()->toolBar->addActions(actions);
            }
        }
        MainWindow::GetSingleton()->GetUI()->toolBar->widgetForAction(action)->setStyleSheet("color: white;");
        if(!MainWindow::GetSingleton()->GetUI()->toolBar->isVisible())
            MainWindow::GetSingleton()->GetUI()->toolBar->setVisible(true);
    }
}

bool Java_GetActionState(JNIEnv* env, jclass clazz, jint actionId)
{
    QAction* action = NULL;
    if(jActions.find(actionId) != jActions.end())
        action = jActions[actionId];
    if(action)
        return action->isChecked();
    return false;
}

void Java_SetActionState(JNIEnv* env, jclass clazz, jint actionId, jboolean state)
{
    QAction* action = NULL;
    if(jActions.find(actionId) != jActions.end())
        action = jActions[actionId];

    if(action)
    {
        GuiOp op = GuiOp([](GuiOp* op) ->
        void {
            ((QAction*)op->ud)->setChecked(!!op->arg);
        }, (void*)action, (int)state);
        MainWindow::GetSingleton()->PushOp(op);
    }

}

void Java_SetWindowCaption(JNIEnv* env, jclass clazz, jstring caption)
{
    MainWindow::GetSingleton()->setWindowTitle(JStringToString(caption).c_str());
}

void Java_ShowMessageBox(JNIEnv* env, jclass clazz, jstring caption, jstring text)
{
    GuiOp op = GuiOp([](GuiOp* op) ->
    void {
        QMessageBox::information(MainWindow::GetSingleton(), op->tag, op->text);
    });
    op.tag = JStringToString(caption).c_str();
    op.text = JStringToString(text).c_str();
    MainWindow::GetSingleton()->PushOp(op);

}

// ------------------------------------------------------------- //

JNINativeMethod CreateBinding(void* fptr, const char* funcName, const char* signature)
{
    JNINativeMethod methodBinding;
    methodBinding.fnPtr = fptr;
    methodBinding.name = (char*)funcName;
    methodBinding.signature = (char*)signature;
    return methodBinding;
}

using namespace std;
bool RegisterJavaBindings(JNIEnv* env)
{
    std::vector<JNINativeMethod> bindings;

    bindings.push_back(CreateBinding((void*)Java_Debug_println,       "println",            "(Ljava/lang/String;Z)V"));
    bindings.push_back(CreateBinding((void*)Java_Debug_setStatusTextLeft, "setStatusTextLeft",      "(Ljava/lang/String;)V"));
    bindings.push_back(CreateBinding((void*)Java_Debug_setStatusTextRight, "setStatusTextRight",      "(Ljava/lang/String;)V"));
    bindings.push_back(CreateBinding((void*)Java_RequestUICallback,   "requestUICallback",  "(Ljava/lang/String;I)V"));

    bindings.push_back(CreateBinding((void*)Java_CreateMenu_SI,  "createMenu",      "(Ljava/lang/String;I)I"));
    bindings.push_back(CreateBinding((void*)Java_CreateMenu_S,   "createMenu",      "(Ljava/lang/String;)I"));
    bindings.push_back(CreateBinding((void*)Java_CreateAction,   "createAction",    "(Ljava/lang/String;ILjava/lang/String;IZZ)I"));
    bindings.push_back(CreateBinding((void*)Java_AddMenuSeparator,   "addMenuSeparator",  "(I)V"));
    bindings.push_back(CreateBinding((void*)Java_AddToolBarSeparator,   "addToolBarSeparator",  "()V"));
    bindings.push_back(CreateBinding((void*)Java_AddActionToToolBar,   "addActionToToolBar",  "(II)V"));
    bindings.push_back(CreateBinding((void*)Java_GetActionState,        "getActionState", "(I)Z"));
    bindings.push_back(CreateBinding((void*)Java_SetActionState,        "setActionState", "(IZ)V"));
    bindings.push_back(CreateBinding((void*)Java_SetWindowCaption, "setWindowCaption", "(Ljava/lang/String;)V"));
    bindings.push_back(CreateBinding((void*)Java_ShowMessageBox, "showMessageBox", "(Ljava/lang/String;Ljava/lang/String;)V"));

    jclass cNativeItf = env->FindClass(DANKSCAPE_NATIVEINTERFACE_CLASS);
    if(!cNativeItf)
    {
        cout << "ERROR: Could not find class '" << DANKSCAPE_NATIVEINTERFACE_CLASS << "'!" << endl;
        return false;
    }
    JManager::GetSingleton()->CatchExceptions();
    MainWindow::GetSingleton()->PrintDebugString("[JavaBindingsRegistrar]: Registering native methods...\n");
    env->RegisterNatives(cNativeItf, &bindings[0], bindings.size());
    JManager::GetSingleton()->CatchExceptions();
    MainWindow::GetSingleton()->PrintDebugString("[JavaBindingsRegistrar]: Done.\n");
    return true;
}
