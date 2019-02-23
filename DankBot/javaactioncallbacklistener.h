#ifndef JAVAACTIONCALLBACKLISTENER_H
#define JAVAACTIONCALLBACKLISTENER_H

#include <QObject>
#include <qsignalmapper>
#include <string>
#include <jni.h>
#include <QAction>

class JavaActionCallbackListener : public QObject
{
    Q_OBJECT

    static JavaActionCallbackListener* singleton;

    QSignalMapper* triggeredMapper = NULL;
    QSignalMapper* toggledMapper = NULL;
public:
    explicit JavaActionCallbackListener(QObject* parent = 0);
    virtual ~JavaActionCallbackListener();
    static JavaActionCallbackListener* GetSingleton();

    void BindCallback(QAction* action, int id);

private slots:
    void onActionTriggered(int id);

    void onActionToggled(int id);
};

#endif // JAVAACTIONCALLBACKLISTENER_H
