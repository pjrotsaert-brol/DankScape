#-------------------------------------------------
#
# Project created by QtCreator 2016-12-06T01:42:45
#
#-------------------------------------------------

QT       += core gui network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = DankBot
TEMPLATE = app


SOURCES += main.cpp\
        Widgets/mainwindow.cpp \
    urldownloader.cpp \
    jmanager.cpp \
    debugstream.cpp \
    debugwriter.cpp \
    Widgets/appletframe.cpp \
    utils.cpp \
    Widgets/consoleprompt.cpp \
    Widgets/overlay.cpp \
    dankscape.cpp \
    javabindings.cpp \
    javaactioncallbacklistener.cpp

HEADERS  += Widgets/mainwindow.h \
    urldownloader.h \
    jmanager.h \
    debugstream.h \
    debugwriter.h \
    Widgets/appletframe.h \
    utils.h \
    Widgets/consoleprompt.h \
    Widgets/overlay.h \
    dankscape.h \
    Widgets/guieventop.h \
    javaactioncallbacklistener.h

FORMS    += mainwindow.ui

LIBS += -L$$PWD/../JNI/lib/ -ljvm
LIBS += -L$$PWD/../JNI/lib/ -ljawt
 LIBS += -lws2_32

INCLUDEPATH += $$PWD/../JNI/include
DEPENDPATH += $$PWD/../JNI/include

RESOURCES += \
    icons.qrc


