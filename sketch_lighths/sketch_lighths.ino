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

drawFunction draw;

void save(uint8_t *data, uint8_t len)
{
  EEPROM.write(0, len);
  for(int i = 0; i < len; i++)
  {
    EEPROM.write(i + 1, data[i]);
  }
}

void load(uint8_t *data)
{
  int len = EEPROM.read(0);
  
  for(int i = 0; i < len; i++)
  {
    data[i] = EEPROM.read(i + 1);
  }
  
  //data[0] = 3;
}

void updateMode()
{
  draw = 0;
  switch (msg.data[0])
  {
  case 0x01:
    draw = ((ModeSingleColor*)data)->init(msg.data + 1);
    break;

  case 0x02:
    draw = ((ModePointsRGB*)data)->init();
    break;
    
  case 0x03:
    draw = ((ModeAurora*)data)->init();
    break;

  case 0x04:
    draw = ((ModeProg*)data)->init(msg.data + 1, msg.position - 1);
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
  load(msg.data);
  updateMode();
}

void loop() 
{
  if(msg.isMessage() > 0)
  {
    save(msg.data, msg.position);
    updateMode();
  }

  if(draw != 0)
  {
    draw(screen);
  }

  screen.sendToModules();

  delay(1);
}


