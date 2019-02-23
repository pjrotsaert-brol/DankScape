#ifndef GUIEVENTOP_H
#define GUIEVENTOP_H

#include <qstring>

#define OP_DEBUGPRINT       0
#define OP_SETSTATUSTEXT    1
#define OP_GENERICFUNC      2

#define STATUSTEXT_LEFT  0
#define STATUSTEXT_RIGHT 1

#include <jni.h>
struct GuiEventOp
{
    int opcode;

    int arg = 0;
    int statusTextLocation = 0;
    QString text, title, tag;
    bool replaceLastLine = false;

    JNIEnv* env;
    jclass clazz;

    std::string str;
    void* ud = NULL;

    void (*fptr)(GuiEventOp* op) = NULL;

    GuiEventOp(){}
    GuiEventOp(int _opcode) : opcode(_opcode) {}
    GuiEventOp(void (*fnptr)(GuiEventOp*), JNIEnv* _env = NULL, jclass _class = NULL, int _arg = 0, std::string _strArg = "")
    {
        clazz = _class;
        str = _strArg;
        env = _env;
        arg = _arg;
        fptr = fnptr;
        opcode = OP_GENERICFUNC;
    }
    GuiEventOp(void (*fnptr)(GuiEventOp*), void* userdata, int _arg = 0)
    {
        ud = userdata;
        arg = _arg;
        fptr = fnptr;
        opcode = OP_GENERICFUNC;
    }

};

typedef GuiEventOp GuiOp;

#endif // GUIEVENTOP_H
