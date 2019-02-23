#include "consoleprompt.h"
#include <iostream>
#include "jmanager.h"
#include "mainwindow.h"

ConsolePrompt::ConsolePrompt(QWidget *parent) : QLineEdit(parent)
{
    setStyleSheet("border: 1px solid rgb(2, 16, 33); background: solid #151d33; border-top: 0px solid black;"

                  "color: white;");
    setMinimumHeight(25);
}

void ConsolePrompt::mousePressEvent(QMouseEvent *event)
{
    //clearFocus(); // Clearing and resetting focus prevents buggy behaviour caused by the 'foreign' applet
    setFocus();
}
