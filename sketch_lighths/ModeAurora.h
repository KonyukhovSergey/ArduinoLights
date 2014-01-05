#ifndef MODE_AURORA_H
#define MODE_AURORA_H

#include <Arduino.h>

#include "Screen.h"
#include "ModeBase.h"

void drawAurora(Screen &screen);

struct ModeAurora
{
  float t;

  drawFunction init()
  {
    t = 0;
    return drawAurora;
  }

  void draw(Screen &screen)
  {
    t += 0.1f;

    for(int i = 0; i < SCREEN_PIXELS; i++)
    {
      float x = i;
      screen.pixels[i].r = 64.0f * sin(x * 0.2 + 3 * sin(t*0.01)) * cos(t*0.1) + 63.0f;
      screen.pixels[i].g = 128.0f * sin(x * (0.4+0.2*cos(t*.01)) + 4 * sin(t*0.02+2)) * cos(t*0.1+2) + 128.0f;
      screen.pixels[i].b = 128.0f * sin(x * (0.4+0.2*cos(t*.01)) + 3.5 * sin(t*0.015+5)) * cos(t*0.14+2) + 128.0f;
    }
  }
};

#endif



