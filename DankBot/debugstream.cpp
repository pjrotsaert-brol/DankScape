#include "debugstream.h"
#include "Widgets/mainwindow.h"

DebugStream::DebugStream()
{

}

void DebugStream::SetDebugTag(std::string tag)
{
    debugTag = tag;
}


DebugStream& DebugStream::operator<<(unsigned int other)
{
    buf += std::to_string(other);
    return *this;
}
DebugStream& DebugStream::operator<<(int64_t other)
{
    buf += std::to_string(other);
    return *this;
}
DebugStream& DebugStream::operator<<(uint64_t other)
{
    buf += std::to_string(other);
    return *this;
}
DebugStream& DebugStream::operator<<(int other)
{
    buf += std::to_string(other);
    return *this;
}
DebugStream& DebugStream::operator<<(float other)
{
    buf += std::to_string(other);
    return *this;
}
DebugStream& DebugStream::operator<<(double other)
{
    buf += std::to_string(other);
    return *this;
}

DebugStream& DebugStream::operator<<(const char* other)
{
    buf += other;
    return *this;
}

DebugStream& DebugStream::operator<<(std::string& other)
{
    buf += other;
    return *this;
}
DebugStream& DebugStream::operator<<(const QString& other)
{
    buf += other.toStdString();
    return *this;
}

DebugStream& DebugStream::operator<<(bool& other)
{
    buf += other ? "true" : "false";
    return *this;
}
DebugStream& DebugStream::operator<<(char& other)
{
    buf += other;
    return *this;
}
DebugStream& DebugStream::operator<<(DebugToken other)
{
    if(debugTag.size() > 0)
        buf = std::string("[") + debugTag + "]: " + buf;

    if(other == ENDL)
        MainWindow::GetSingleton()->PrintDebugString((buf + "\n").c_str());
    else if(other == REPL)
        MainWindow::GetSingleton()->PrintDebugString((buf + "\n").c_str(), true);
    else if(other == FLUSH)
        MainWindow::GetSingleton()->PrintDebugString(buf.c_str());

    buf.clear();
    return *this;
}
