#ifndef ARGB_H
#define ARGB_H

#include <Arduino.h>

struct ARGB
{
  uint8_t a;
  uint8_t r;
  uint8_t g;
  uint8_t b;
  
  void fromInt(uint32_t color);
};


#endif
