#ifndef MESSAGE_H
#define MESSAGE_H

#define DATA_NONE 0
#define DATA_RECEIVING 1
#define DATA_READY 2

struct DataReceiver
{
  uint16_t len;
  uint8_t size;
  uint8_t xorValue;

  uint8_t data();

  void init()
  {
    len = 0;
    size = 0;
  }
};

#endif

