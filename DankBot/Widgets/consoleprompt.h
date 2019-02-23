#ifndef CONSOLEPROMPT_H
#define CONSOLEPROMPT_H

#include <QObject>
#include <QWidget>
#include <QLineEdit>

class ConsolePrompt : public QLineEdit
{
protected:
    virtual void mousePressEvent(QMouseEvent *event);
public:
    ConsolePrompt(QWidget* parent);

};

#endif // CONSOLEPROMPT_H
