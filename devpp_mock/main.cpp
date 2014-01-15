#include <windows.h>

#include <SPI.h>
#include <EEPROM.h>
#include <SerialClass.h>

#include "DataReciever.h"
#include "LightMachine.h"
#include "Screen.h"
DataReciever dataReciever;
LightMachine lightMachine;

Screen screen;

void setup() 
{
  Serial.begin(9600);
  SPI.begin();
  SPI.setBitOrder(MSBFIRST);
  SPI.setDataMode(SPI_MODE0); 
  SPI.setClockDivider(SPI_CLOCK_DIV16);
  delay(1);
  dataReciever.init();
  lightMachine.init(&screen);
}

void loop() 
{
  if(dataReciever.data() > 0)
  {
  	lightMachine.init(&screen);
  }
  
  lightMachine.execute();
  
  screen.send();

 // delay(1);
}

int main(int argc, char** argv)
 {
	Serial.set("\x70\xc0\x41\xc8\x00\x00\x80\x09\xc0\x00\x00\x00\x00\x81\x41\xc0\x43\x7f\x00\x00\x41\xc0\x40\xa0\x00\x00\x03\xc0\x43\x7f\x00\x00\x41\xc0\x40\xa0\x00\x00\x03\x02\x19\x41\xc0\x3f\x80\x00\x00\x01\x81\x41\xc0\x42\x48\x00\x00\x16\xc0\x00\x00\x00\x43\x13\xc0\x00\x00\x00\x0d\x12\x40\xc0\x43\x7f\x00\x00\xc0\x43\x7f\x00\x00\xc0\x43\x7f\x00\x00\x19\x40\xc0\x3f\x80\x00\x00\x01\x80\x40\xc0\x42\x44\x00\x00\x15\xc0\x00\x00\x00\x6f\x13\xc0\x00\x00\x00\x00\x80\x14", 112 + 2);

	setup();
	

	while(true)
	{
		loop();
		SPI.draw();
	}
	
	return 0;
}
