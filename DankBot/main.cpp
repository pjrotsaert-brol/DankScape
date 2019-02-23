#include "Widgets/mainwindow.h"
#include <QApplication>
#include "dankscape.h"

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    MainWindow::GetSingleton()->show();

    DankScape::GetSingleton()->Init();

    return a.exec();
}
