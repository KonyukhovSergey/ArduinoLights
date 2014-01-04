#include "ARGB.h"

void ARGB::fromInt(uint32_t color)
{
  a = (color >> 24) & 0xff;
  r = (color >> 16) & 0xff;
  g = (color >> 8) & 0xff;
  b = (color ) & 0xff;
}


