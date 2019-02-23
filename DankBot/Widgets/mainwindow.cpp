#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <QPlainTextEdit>
#include <QScrollBar>
#include <QUrl>
#include <QtNetwork/QNetworkRequest>
#include <QtNetwork/QNetworkAccessManager>
#include "urldownloader.h"
#include <jni.h>
#include <iostream>
#include <QJsonDocument>
#include <qjsonobject.h>
#include <qjsonarray.h>
#include <qmessagebox.h>
#include "jmanager.h"
#include "utils.h"
#include "dankscape.h"
#include <qactiongroup.h>

MainWindow* MainWindow::m_singleton = nullptr;

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent), DebugWriter("Bot GUI"), ui(new Ui::MainWindow)
{
    m_singleton = this;
    ui->setupUi(this);

    ui->txtDebug->setStyleSheet("border: 1px solid rgb(2, 16, 33); background: solid #151d33; \n\n}\n\n/*****  SCROLLBAR STYLESHEET *****/\n\n\n QScrollBar:horizontal {\n     border: 2px solid rgb(239, 239, 242);\n\n\n     background: solid rgb(239, 239, 242);\n     height: 15px;\n     margin: 0px 20px 0px 20px;\n }\n\n QScrollBar::add-line:horizontal {\n     border: 2px solid rgb(239, 239, 242);\n	/*border-right: 1px solid rgb(206, 207, 220);\n	right: -2px;*/\n     background: rgb(239, 239, 242);\n     width: 16px;\n     subcontrol-position: right;\n     subcontrol-origin: margin;\n\n }\n\n QScrollBar::sub-line:horizontal {\n     border: 2px solid rgb(239, 239, 242);\n     background: rgb(239, 239, 242);\n     width: 16px;\n     subcontrol-position: left;\n     subcontrol-origin: margin;\n }\n\nQScrollBar::right-arrow:horizontal {\n	 background-image: url(:/icons/res/scrollbar_right.png);\n     width: 15px;\n     height: 15px;\n }\n\nQScrollBar:left-arrow:horizontal {\n	 background-image: url(:/icons/res/scrollbar_left.png);\n     width: 15px;\n     height: 15px;\n }\n\n\n QScrollBar::add-page:horizontal, QScrollBar::sub-page:horizontal {\n     background: none;\n }\n\n\n\n QScrollBar::handle:horizontal {\n     background:rgb(173, 174, 184);\n     min-width: 16px;\n }\n\n QScrollBar:vertical {\n     border: 2px solid rgb(239, 239, 242);\n     background: solid rgb(239, 239, 242);\n     width: 15px;\n     margin: 20px 0px 20px 0px;\n }\n\nQScrollBar::add-line:vertical {\n     border: 2px solid rgb(239, 239, 242);\n     background: rgb(239, 239, 242);\n     height: 16px;\n     subcontrol-position: bottom;\n     subcontrol-origin: margin;\n }\n\n\n QScrollBar::sub-line:vertical {\n     border: 2px solid rgb(239, 239, 242);\n     background: rgb(239, 239, 242);\n     height: 16px;\n     subcontrol-position: top;\n     subcontrol-origin: margin;\n\n }\n\n QScrollBar::add-page:vertical, QScrollBar::sub-page:vertical {\n     background: none;\n }\n\n\n QScrollBar::handle:vertical {\n     background:  rgb(173, 174, 184);\n     min-height: 16px;\n }\n\n QScrollBar::handle:hover {\n     background: rgb(66, 133, 244);\n     min-width: 16px;\n }\n\n\n\nQScrollBar:up-arrow:vertical {\n	 background-image: url(:/icons/res/scrollbar_up.png);\n     width: 15px;\n     height: 15px;\n	margin-top: 1px;\n }\n\n\nQScrollBar::down-arrow:vertical {\n	 background-image: url(:/icons/res/scrollbar_down.png);\n     width: 15px;\n     height: 15px;\n }\n\nQScrollBar:left-arrow:horizontal:hover {\n	 background-image: url(:/icons/res/scrollbar_left_hover.png);\n }\nQScrollBar::right-arrow:horizontal:hover {\n	 background-image: url(:/icons/res/scrollbar_right_hover.png);\n }\n\nQScrollBar:up-arrow:vertical:hover {\n	 background-image: url(:/icons/res/scrollbar_up_hover.png);\n }\nQScrollBar::down-arrow:vertical:hover {\n	 background-image: url(:/icons/res/scrollbar_down_hover.png);\n }\n\n/*****************************************/\n\n");

    appletFrame = new AppletFrame(this);
    ui->frameLayout->addWidget(appletFrame);

    consolePrompt = new ConsolePrompt(this);
    ui->consoleDockLayout->addWidget(consolePrompt);

    statusLabelLeft = new QLabel("");
    statusLabelRight = new QLabel("");
    ui->statusBar->addWidget(statusLabelLeft);
    ui->statusBar->addWidget(new QLabel(""), 1000);
    ui->statusBar->addWidget(statusLabelRight);

    toolbarSpacer = new QWidget();
    toolbarSpacer->setSizePolicy(QSizePolicy::Expanding, QSizePolicy::Preferred);
    toolbarSpacerAction = ui->toolBar->addWidget(toolbarSpacer);

    ui->toolBar->setVisible(false);
    ui->menuBar->setVisible(false);

    heartbeatTimer = new QTimer(this);
    heartbeatTimer->setInterval(10);
    heartbeatTimer->start();
    connect(heartbeatTimer, SIGNAL(timeout()), this, SLOT(GuiEventHeartbeat()));
}

MainWindow::~MainWindow()
{
    delete ui;
}

MainWindow* MainWindow::GetSingleton()
{
    if(!m_singleton)
        m_singleton = new MainWindow();
    return m_singleton;
}

QAction* MainWindow::GetToolBarSpacer()
{
    return toolbarSpacerAction;
}

AppletFrame* MainWindow::GetAppletFrame()
{
    return appletFrame;
}
ConsolePrompt* MainWindow::GetConsolePrompt()
{
    return consolePrompt;
}

void MainWindow::mousePressEvent(QMouseEvent *event)
{
    QMainWindow::mousePressEvent(event);
}

bool MainWindow::eventFilter(QObject *obj, QEvent *event)
{
    return QMainWindow::eventFilter(obj, event);
}

void MainWindow::SetStatusTextLeft(QString text)
{
    statusLabelLeft->setText(text);
}

void MainWindow::SetStatusTextRight(QString text)
{
    statusLabelRight->setText(text);
}

Ui::MainWindow* MainWindow::GetUI()
{
    return ui;
}

void MainWindow::GuiEventHeartbeat()
{
    JManager::GetSingleton()->CatchExceptions();

    std::vector<GuiEventOp> ops;
    eventMutex.lock();
    ops = opQueue;
    opQueue.clear();
    eventMutex.unlock();

    for(GuiEventOp op : ops)
    {
        switch(op.opcode)
        {
        case OP_DEBUGPRINT:
            PrintDebugString(op.text, op.replaceLastLine);
            break;
        case OP_SETSTATUSTEXT:
            if(op.statusTextLocation == STATUSTEXT_LEFT)
                SetStatusTextLeft(op.text);
            else
                SetStatusTextRight(op.text);
            break;
        case OP_GENERICFUNC:
            if(op.fptr)
                op.fptr(&op);
            break;
        }
    }
}

void MainWindow::PushOp(GuiEventOp &op)
{
    eventMutex.lock();
    opQueue.push_back(op);
    eventMutex.unlock();
}

void MainWindow::PrintDebugString(QString text, bool replaceLastLine)
{
    std::cout << "CONSOLE: " << text.toStdString() << std::flush;

    if(replaceLastLine)
    {
        auto idx = debugBacklog.toStdString().rfind("\n");
        idx = debugBacklog.toStdString().rfind("\n", idx-1);
        debugBacklog = debugBacklog.toStdString().substr(0, idx + 1).c_str();
    }

    debugBacklog += text;

    if(debugBacklog.size() > MAX_DEBUG_BACKLOG_CHARS)
    {
        auto firstNewline = debugBacklog.toStdString().find("\n", debugBacklog.size() - MAX_DEBUG_BACKLOG_CHARS);
        if(firstNewline == std::string::npos)
            firstNewline = debugBacklog.size() - MAX_DEBUG_BACKLOG_CHARS;
        debugBacklog = debugBacklog.toStdString().substr(firstNewline + 1).c_str();
    }
    ui->txtDebug->setPlainText(debugBacklog);
    ui->txtDebug->verticalScrollBar()->setValue(ui->txtDebug->verticalScrollBar()->maximum() - 1);

    ui->txtDebug->repaint();
    //ui->txtDebug->scroll(0, -99999999);
}
