#ifndef DEBUGSTREAM_H
#define DEBUGSTREAM_H

#include <string>
#include <qstring.h>

class DebugStream
{
    std::string buf;
    std::string debugTag;

public:
    enum DebugToken
    {
        ENDL,
        REPL,
        FLUSH
    };

    DebugStream();

    void SetDebugTag(std::string tag);

    DebugStream& operator<<(unsigned int other);
    DebugStream& operator<<(int64_t other);
    DebugStream& operator<<(uint64_t other);
    DebugStream& operator<<(int other);
    DebugStream& operator<<(float other);
    DebugStream& operator<<(double other);
    DebugStream& operator<<(const char* other);
    DebugStream& operator<<(std::string& other);
    DebugStream& operator<<(const QString& other);
    DebugStream& operator<<(bool& other);
    DebugStream& operator<<(char& other);
    DebugStream& operator<<(DebugToken other);

};

#endif // DEBUGSTREAM_H
