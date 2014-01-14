#include <Arduino.h>
#include <SerialClass.h>
#include <EEPROM.h>
#include "DataReciever.h"

uint8_t DataReciever::data()
{
  while(Serial.available() > 0)
  {
    int dataByte = Serial.read();

    if(size == 0)
    {
      if(dataByte == 0)
      {
        EEPROM.write(len, 0);
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
      EEPROM.write(len, dataByte);
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

