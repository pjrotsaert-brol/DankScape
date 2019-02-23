#include "appletframe.h"
#include <QResizeEvent>
#include "jmanager.h"
#include <iostream>
#include "mainwindow.h"
#include <QGridLayout>
#include <QStackedLayout>
#include <qapplication.h>
#include <qwindow.h>

AppletFrame::AppletFrame(QWidget *parent) : QFrame(parent)
{
    setMinimumSize(765, 503);
    setStyleSheet("background: solid black;");
    setLayout(new QStackedLayout());
    layout()->setMargin(0);
}

void AppletFrame::resizeEvent(QResizeEvent *event)
{
    JManager::GetSingleton()->ResizeApplet(event->size().width(), event->size().height());
}

void AppletFrame::mousePressEvent(QMouseEvent *event)
{
    //MainWindow::GetSingleton()->GetConsolePrompt()->clearFocus();
    //setFocus();
    //innerFrame->grabKeyboard();

    QFrame::mousePressEvent(event);
}

int AppletFrame::GetHWND()
{
   return winId();
}

void AppletFrame::paintEvent(QPaintEvent *e)
{
    //std::cout << "lfklg"  << std::endl;
    QFrame::paintEvent(e);
}

void AppletFrame::EmbedWindow(HANDLE hwnd)
{
    appletWindow = QWindow::fromWinId((WId)hwnd);
    applet = QWidget::createWindowContainer(appletWindow);

    /*
    ((QStackedLayout*)layout())->setStackingMode(QStackedLayout::StackAll);

    Overlay *test = new Overlay(this);
    test->setStyleSheet("background: solid red;");
    test->resize(200, 200);
    test->move(100, 100);


    layout()->addWidget(test);*/

    layout()->addWidget(applet);
    ((QStackedLayout*)layout())->setStackingMode(QStackedLayout::StackAll);
    //((QStackedLayout*)layout())->setCurrentWidget(test);

    //test->show();
    //test->raise();
}
