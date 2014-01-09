#ifndef MODE_PROG_H
#define MODE_PROG_H

#include <Arduino.h>

#include "Screen.h"
#include "ModeBase.h"

#include "LightMachine.h"

uint8_t drawProg(Screen &screen);

extern LightMachine lm;

struct ModeProg
{

  float t;
  uint16_t len;

  drawFunction init()
  {
    t = 0;

    len = ((uint16_t)EEPROM.read(0)) | (((uint16_t)(EEPROM.read(1))) << 8);

    lm.init(len - 1);

    return drawProg;
  }

  uint8_t draw(Screen &screen)
  {	
    uint16_t loopPosition = lm.execute(0);

    if(loopPosition == 65535)
    {
      return 0;
    }

    for(int i = 0; i < SCREEN_PIXELS; i++)
    {
      lm.variables['x' - 'a'] = i;

      if(lm.execute(loopPosition) == 65535)
      {
        return 0;
      }

      screen.pixels[i].r = calcColor(lm.variables['r' - 'a']);
      screen.pixels[i].g = calcColor(lm.variables['g' - 'a']);
      screen.pixels[i].b = calcColor(lm.variables['b' - 'a']);

    }

    return 1;
  }

  uint8_t calcColor(float value)
  {
    if(value < 0)
    {
      return 0;
    }

    if(value > 255)
    {
      return 255;
    }

    return value;
  }
};

#endif



