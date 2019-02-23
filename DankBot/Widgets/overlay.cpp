#include "overlay.h"
#include "appletframe.h"
#include <QResizeEvent>
#include "jmanager.h"
#include <iostream>
#include "mainwindow.h"
#include <QGridLayout>
#include <QStackedLayout>

Overlay::Overlay(QWidget *parent) : QFrame(parent)
{
}

void Overlay::resizeEvent(QResizeEvent *event)
{
}

void Overlay::mousePressEvent(QMouseEvent *event)
{
    std::cout << "aaaaaaaaaaaaaaa" << std::endl;
    //this->setFocus();
    //MainWindow::GetSingleton()->GetConsolePrompt()->releaseKeyboard();
}
