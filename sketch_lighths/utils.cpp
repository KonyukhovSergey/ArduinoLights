#include "utils.h"

uint8_t blendAdd(uint8_t a, uint8_t s, uint8_t d)
{
  uint16_t v = ((a * s) >> 8) + d;

  if(v >= 255)
  {
    v = 255;
  }

  return v;
}

uint8_t fadeValue(uint8_t value, uint8_t count)
{
  if(value > count)
  {
    return value - count;
  }
  else
  {
    return 0;
  }
}



