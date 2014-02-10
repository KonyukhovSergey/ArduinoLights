#include <SPI.h>
#include <EEPROM.h>

#include "DataReceiver.h"
#include "LightMachine.h"
#include "Screen.h"

#define NORMAL_EXECUTION_STATE_ADDRESS 1023

uint8_t pixels[SCREEN_PIXELS * 3];

DataReceiver dataReceiver;
LightMachine lightMachine;

Screen screen;

void initExec()
{
  EEPROM.write(NORMAL_EXECUTION_STATE_ADDRESS, 39);
  lightMachine.init(&screen);
  EEPROM.write(NORMAL_EXECUTION_STATE_ADDRESS, 28);
}

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
  
  if(EEPROM.read(NORMAL_EXECUTION_STATE_ADDRESS) == 28)
  {
    dataReceiver.init();
    initExec();
  }
}

void loop() 
{
  switch(dataReceiver.data())
  {
  case DATA_READY:
    initExec();
    break;

  case DATA_RECEIVING:
    lightMachine.stop();
    break;
  }
  
  if(EEPROM.read(NORMAL_EXECUTION_STATE_ADDRESS) == 28)
  {
    lightMachine.execute();
  }

  screen.send();
}



