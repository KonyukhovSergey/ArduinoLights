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
  uint8_t byteCode[240];
  uint8_t length;

  drawFunction init(uint8_t *data, int len)
  {
    t = 0;
    
    for(int i=0; i<len; i++)
	{
		byteCode[i] = data[i];
	}
	
	length = len;
    
    lm.init(byteCode, len);
    
    return drawProg;
  }

  void draw(Screen &screen)
  {
  	t += 0.1f;
  	
    for(int i = 0; i < SCREEN_PIXELS; i++)
    {
    	lm.variables['x'-'a'] = i;
    	lm.variables['t'-'a'] = t;
    	
    	lm.execute();

		screen.pixels[i].r = lm.variables['r'-'a'];
		screen.pixels[i].g = lm.variables['g'-'a'];
		screen.pixels[i].b = lm.variables['b'-'a'];

    	//execute();
    }
  }
};

#endif
