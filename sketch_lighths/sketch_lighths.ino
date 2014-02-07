#include <SPI.h>
#include <EEPROM.h>

#include "DataReciever.h"
#include "LightMachine.h"
#include "Screen.h"

#define EEPROM_SIZE 1024

uint8_t pixels[SCREEN_PIXELS * 3];

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
  randomSeed(analogRead(0));
  
  screen.init(pixels);
  
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
}

