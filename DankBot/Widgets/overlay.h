#ifndef OVERLAY_H
#define OVERLAY_H

#include <QObject>
#include <QWidget>
#include <QFrame>
#include <QMouseEvent>

class Overlay : public QFrame
{
protected:

    virtual void resizeEvent(QResizeEvent *event);
    virtual void mousePressEvent(QMouseEvent* event);

public:
    Overlay(QWidget* parent = NULL);

};

#endif // OVERLAY_H
