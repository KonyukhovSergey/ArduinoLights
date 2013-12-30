#ifndef MESSAGE_H
#define MESSAGE_H

struct Message
{
  uint8_t data[256];
  uint8_t length;
  uint8_t position;
  
  uint8_t isMessage();
};

#endif
