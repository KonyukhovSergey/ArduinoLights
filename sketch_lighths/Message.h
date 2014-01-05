#ifndef MESSAGE_H
#define MESSAGE_H

struct Message
{
  uint16_t len;
  uint8_t size;
  uint8_t xorValue;

  uint8_t isMessage();

  void init()
  {
    len = 0;
    size = 0;
  }
};

#endif

