#ifndef MODE_PROG_H
#define MODE_PROG_H

#include <Arduino.h>

#include "Screen.h"
#include "ModeBase.h"

#include "LightMachine.h"

void drawProg(Screen &screen);

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

  void draw(Screen &screen)
  {	
    uint16_t loopPosition = lm.execute(0);
    
    for(int i = 0; i < SCREEN_PIXELS; i++)
    {
      lm.variables['x' - 'a'] = i;

      lm.execute(loopPosition);

      screen.pixels[i].r = lm.variables['r' - 'a'];
      screen.pixels[i].g = lm.variables['g' - 'a'];
      screen.pixels[i].b = lm.variables['b' - 'a'];
      
    }
  }
};

#endif

