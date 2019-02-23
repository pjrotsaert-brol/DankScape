#include "utils.h"

Utils::Utils()
{

}


std::string Utils::ParseValue(std::string source, std::string begin, std::string end, int offset, int *out_endOffset)
{
    auto iStart = source.find(begin, (size_t)offset);
    auto iEnd   = source.find(end, iStart + begin.size());

    if(out_endOffset)
        *out_endOffset = int(iEnd + end.size());

    return source.substr(iStart + begin.size(), iEnd - (iStart + begin.size()));
}
