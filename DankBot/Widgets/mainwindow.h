#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include "debugwriter.h"
#include <qframe.h>
#include "widgets/appletframe.h"
#include <string>
#include <map>
#include "widgets/consoleprompt.h"
#include <qlabel.h>
#include <qtimer>
#include <mutex>
#include "guieventop.h"
#include <unordered_map>

#define MAX_DEBUG_BACKLOG_CHARS 10000

namespace Ui {
class MainWindow;
}



class MainWindow : public QMainWindow, DebugWriter
{
    Q_OBJECT

    Ui::MainWindow *ui;
    static MainWindow* m_singleton;

    // Widgets
    AppletFrame* appletFrame = NULL;
    ConsolePrompt* consolePrompt = NULL;

    QString debugBacklog;

    QLabel* statusLabelLeft, *statusLabelRight;

    QTimer* heartbeatTimer;

    QWidget* toolbarSpacer;
    QAction* toolbarSpacerAction;

    std::mutex eventMutex;
    std::vector<GuiEventOp> opQueue;



protected:

    virtual void mousePressEvent(QMouseEvent *event);

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

    static MainWindow* GetSingleton();

    ConsolePrompt* GetConsolePrompt();

    AppletFrame *GetAppletFrame();
    void PrintDebugString(QString text, bool replaceLastLine = false);
    void SetStatusTextLeft(QString text);
    void SetStatusTextRight(QString text);

    bool eventFilter(QObject *obj, QEvent *event);

    QAction* GetToolBarSpacer();

    void PushOp(GuiEventOp& op);

    Ui::MainWindow * GetUI();


private slots:
    void GuiEventHeartbeat();
};

#endif // MAINWINDOW_H
