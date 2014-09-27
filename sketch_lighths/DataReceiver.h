#ifndef MESSAGE_H
#define MESSAGE_H
#include <Arduino.h>

#define DATA_RECEIVER_TIMEOUT               250

#define DATA_NONE       0x00
#define DATA_AVAILABLE  0x01
#define DATA_RECEIVING  0x02
#define DATA_READY      0x03
#define DATA_ERROR      0x04

struct DataReceiver
{
    uint16_t len;
    uint8_t size;
    uint8_t xorValue;
    uint8_t *data;

    uint16_t total;

    uint16_t _state;

    uint8_t state();

    void init(uint8_t *data)
    {
        _state = 0;
        len = 0;
        size = 0;
        this->data = data;
    }

    uint16_t read();
};

#endif

