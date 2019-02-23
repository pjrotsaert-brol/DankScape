#ifndef DEBUGWRITER_H
#define DEBUGWRITER_H

#include "debugstream.h"

class DebugWriter
{
protected:
    DebugStream dbg;
    const DebugStream::DebugToken endl  = DebugStream::ENDL;
    const DebugStream::DebugToken flush = DebugStream::FLUSH;
    const DebugStream::DebugToken repl  = DebugStream::REPL;

public:
    DebugWriter();
    DebugWriter(std::string tag);
};

#endif // DEBUGWRITER_H
