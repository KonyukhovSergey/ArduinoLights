#include <Arduino.h>
#include <SerialClass.h>
#include <EEPROM.h>
#include "DataReceiver.h"

uint8_t DataReceiver::data()
{
  uint8_t result = DATA_NONE;
  
  while(Serial.available() > 0)
  {
    result = DATA_RECEIVING;
    
    int dataByte = Serial.read();

    if(size == 0)
    {
      if(dataByte == 0)
      {
        EEPROM.write(len, 0);
        len = 0;
        return DATA_READY;
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

  return result;
}

