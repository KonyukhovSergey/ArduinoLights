#include <EEPROM.h>
#include <SPI.h>

#include "Screen.h"
#include "Message.h"

#include "ModeSingleColor.h"
#include "ModePointsRGB.h"
#include "ModeAurora.h"
#include "ModeProg.h"

Screen screen;
Message msg;
#include <stdio.h>

drawFunction draw;

void updateMode()
{
  draw = 0;

  switch (EEPROM.read(2))
  {
  case 0x01:
    draw = ((ModeSingleColor*)data)->init();
    break;

  case 0x02:
    draw = ((ModePointsRGB*)data)->init();
    break;

  case 0x03:
    draw = ((ModeAurora*)data)->init();
    break;

  case 0x04:
    draw = ((ModeProg*)data)->init();
    break;
  }
}

void setup() 
{
  Serial.begin(9600);
  SPI.begin();
  SPI.setBitOrder(MSBFIRST);
  SPI.setDataMode(SPI_MODE0); 
  SPI.setClockDivider(SPI_CLOCK_DIV16);
  delay(1);
  msg.init();
  updateMode();
}

void loop() 
{
  if(msg.isMessage() > 0)
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



