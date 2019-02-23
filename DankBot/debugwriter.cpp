#include "debugwriter.h"

DebugWriter::DebugWriter()
{

}

DebugWriter::DebugWriter(std::string tag)
{
    dbg.SetDebugTag(tag);
}
