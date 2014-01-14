#include <windows.h>

#include <SPI.h>
#include <EEPROM.h>
#include <SerialClass.h>


#include "DataReciever.h"

DataReciever dataReciever;
LightMachine lightMachine;

void init()
{
  len = ((uint16_t)EEPROM.read(0)) | (((uint16_t)(EEPROM.read(1))) << 8);
  lm.init(len);
}

void setup() 
{
  Serial.begin(9600);
  SPI.begin();
  SPI.setBitOrder(MSBFIRST);
  SPI.setDataMode(SPI_MODE0); 
  SPI.setClockDivider(SPI_CLOCK_DIV16);
  delay(1);
  dataReciever.init();
  init();
}

void loop() 
{
  if(dataReciever.data() > 0)
  {
    updateMode();
  }

  if(draw != 0)
  {
    draw(screen);
  }

  screen.sendToModules();

  delay(1);
}

int main(int argc, char** argv)
 {
	setup();
	
	Serial.set("\x78\x53\xc0\x3c\x23\xd7\x0a\x01\x93\x09\x53\x8f\xc0\x00\x00\x00\x53\x10\x4f\x91\x53\xc0\x40\x00\x00\x00\x03\xc0\x3f\x80\x00\x00\x01\x8f\xc0\x00\x00\x00\x53\x10\x4f\x86\x53\xc0\x40\x40\x00\x00\x03\xc0\x40\x00\x00\x00\x01\x8f\xc0\x00\x00\x00\x53\x10\x4f\x81\x41\xc0\x3f\x80\x00\x00\x16\xc0\x00\x00\x00\x52\x13\xc0\x43\x7f\x00\x00\x81\x14\xc0\x41\xc8\x00\x00\x4f\x06\x03\xc0\x41\xc8\x00\x00\x01\x8f\x57\x4f\x02\x83\xc0\x43\x7f\x00\x00\xc0\x3f\x80\x00\x00\x43\x43\x03\x01\x04\x8f\x11", 120 + 1);

	while(true)
	{
		loop();
		SPI.draw();
	}
	
	return 0;
}
