#include <Arduino.h>
#include <SerialClass.h>
#include <EEPROM.h>
#include "Message.h"

uint8_t Message::isMessage()
{
  while(Serial.available() > 0)
  {
    int dataByte = Serial.read();

    if(size == 0)
    {
      if(dataByte == 0)
      {
        EEPROM.write(0, len & 0xff);
        EEPROM.write(1, len >> 8);
        len = 0;
        return 1;
      }
      else
      {
        size = dataByte;
        xorValue = 0;
      }
    }
    else
    {
      EEPROM.write(len + 2, dataByte);
      len++;

      xorValue = xorValue ^ dataByte;

      size--;

      if(size == 0)
      {
        Serial.write(xorValue);
      }
    }
  }

  return 0;
}

