#include "ModeSingleColor.h"

void drawSingleColor(Screen &screen)
{
	ModeSingleColor *mode = (ModeSingleColor*) data;
	
	for(int i = 0; i < SCREEN_PIXELS; i++)
	{
		screen.pixels[i].r = mode->r;
		screen.pixels[i].g = mode->g;
		screen.pixels[i].b = mode->b;
	}
}

