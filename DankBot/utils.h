#ifndef UTILS_H
#define UTILS_H

#include <string>
#include <unordered_map>



class Utils
{
public:
    Utils();

    static std::string ParseValue(std::string source, std::string begin, std::string end, int offset = 0, int* out_endOffset = NULL);
};

#endif // UTILS_H
