#ifndef MODE_SINGLE_COLOR_H
#define MODE_SINGLE_COLOR_H

#include <Arduino.h>

#include "Screen.h"
#include "ModeBase.h"

void drawSingleColor(Screen &screen);

struct ModeSingleColor
{
  uint8_t r;
  uint8_t g;
  uint8_t b;

  drawFunction init()
  {
    r = EEPROM.read(3+0);
    g = EEPROM.read(3+1);
    b = EEPROM.read(3+2);
    return drawSingleColor;
  }

  void draw(Screen& screen)
  {
    for(int i = 0; i < SCREEN_PIXELS; i++)
    {
      screen.pixels[i].r = r;
      screen.pixels[i].g = g;
      screen.pixels[i].b = b;
    }
  }
};

#endif

