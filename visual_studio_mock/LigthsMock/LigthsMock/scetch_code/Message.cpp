#include <Arduino.h>
#include <SerialClass.h>
#include "Message.h"


uint8_t Message::isMessage()
{
  if(Serial.available() > 0)
  {
    int dataByte = Serial.read();
    
    if(length == 0)
    {
      length = dataByte;
      position = 0;
    }
    else
    {
      data[position] = dataByte;
      
      position++;
      
      if(position == length)
      {
        length = 0;
        return position;
      }
    }
  }
  return 0;
}
