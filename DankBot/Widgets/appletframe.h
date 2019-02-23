#ifndef APPLETFRAME_H
#define APPLETFRAME_H

#include <QObject>
#include <QWidget>
#include <QFrame>
#include <QMouseEvent>
#include "Widgets/overlay.h"
#include <QFocusEvent>
#include <windows.h>

class AppletFrame : public QFrame
{
protected:

    QFrame* innerFrame;
    Overlay* overlay;
    QWidget* applet = NULL;
    QWindow* appletWindow = NULL;

    virtual void resizeEvent(QResizeEvent *event);
    virtual void paintEvent(QPaintEvent *e);

public:
    AppletFrame(QWidget* parent = NULL);

    virtual void mousePressEvent(QMouseEvent* event);
    int GetHWND();
    void EmbedWindow(HANDLE hwnd);
};

#endif // APPLETFRAME_H
