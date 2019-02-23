#ifndef URLDOWNLOADER_H
#define URLDOWNLOADER_H
#include <QObject>
#include <QString>
#include <QNetworkAccessManager>
#include <QNetworkReply>

#include "debugwriter.h"

class URLDownloader : public QObject, DebugWriter
{
    Q_OBJECT

    QNetworkAccessManager* manager;
    QString targetUrl, targetFile;

    static URLDownloader* singleton;
    bool silent = false;

public:
    explicit URLDownloader();
    ~URLDownloader();

    static URLDownloader* GetSingleton(){ if(!singleton){ singleton = new URLDownloader(); }; return singleton; }

    void DownloadFile(QString url, QString filename, void (*callbackFunc)(void *, bool), void *cbData, bool _silent = false);
    void DownloadString(QString url, void (*callbackFunc)(void*, QString, bool), void *cbData, bool _silent = true);

signals:

public slots:

    void DownloadFinished(QNetworkReply* data);
    void DownloadProgress(qint64 received, qint64 total);
};

#endif // URLDOWNLOADER_H
