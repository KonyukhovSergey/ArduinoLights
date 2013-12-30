// LigthsMock.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <windows.h>

#include <SPI.h>
#include <SerialClass.h>


#include "utils.h"
#include "ARGB.h"
#include "PointRGB.h"
#include "Screen.h"
#include "Message.h"
 
#define MODE_SINGLE_COLOR = 0x01

Screen screen;
Message msg;
float t;

struct ModeSingleColor
{
  uint8_t r,g,b;
};

struct ModePointRGB
{
  PointRGB p;
  PointRGB pr;
  PointRGB pg;
  PointRGB pb;
  
};


PointRGB p;
PointRGB pr;
PointRGB pg;
PointRGB pb;



void setup() 
{
  //Serial.begin(115200);
  //SPI.begin();
  //SPI.setBitOrder(MSBFIRST);
  //SPI.setDataMode(SPI_MODE0); 
  //SPI.setClockDivider(SPI_CLOCK_DIV16);
  delay(1);
  
  p.init(255, 255, 255, 25, 0, 8);
  pr.init(255,0,0, 0,0,10);
  pg.init(0,255,0, 0,0,10);
  pb.init(0,0,255, 0,0,10);

}

void loop() 
{
  t += 0.0025f;
  

  screen.clear(0);
    
  p.pos = 25 + 25 * sin(t*10.0f);
  screen.renderPoint(&p);
    
  pr.pos = 25 + 25 * sin(t * 2.0f + 0.0f);
  screen.renderPoint(&pr);

  pg.pos = 25 + 25 * sin(t * 3.0f + 1.0f);
  screen.renderPoint(&pg);
    
  pb.pos = 25 + 25 * sin(t * 5.0f + 2.0f);
  screen.renderPoint(&pb);
  
  screen.sendToModules();
  
  delay(1);
}




int _tmain(int argc, _TCHAR* argv[])
{
	setup();

	while(true)
	{
		loop();
		SPI.draw();
	}


	return 0;
}

