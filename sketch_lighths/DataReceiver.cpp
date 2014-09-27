#include <Arduino.h>
#include <SerialClass.h>
#include <EEPROM.h>
#include "DataReceiver.h"

#define DATA_RECEIVER_STATE_NONE            0x00
#define DATA_RECEIVER_STATE_39              0x01
#define DATA_RECEIVER_STATE_TOTAL           0x02
#define DATA_RECEIVER_STATE_RECEIVING       0x03

uint16_t DataReceiver::read()
{
    int timeout = DATA_RECEIVER_TIMEOUT;

    while(timeout > 0)
    {
        if(Serial.available())
        {
            return Serial.read();
        }
        delay(1);
        timeout--;
    }
    return 256;
}

uint8_t DataReceiver::state()
{
    switch(_state)
    {
    case DATA_RECEIVER_STATE_NONE:
        if(Serial.available())
        {
            if(Serial.read() == 0x28)
            {
                _state = DATA_RECEIVER_STATE_39;
            }
        }
        break;

    case DATA_RECEIVER_STATE_39:
        if(read() == 0x39)
        {
            _state = DATA_RECEIVER_STATE_TOTAL;
        }
        break;

    case DATA_RECEIVER_STATE_TOTAL:
    {
        int dataByte = read();

        if(dataByte < 256)
        {
            total = dataByte * 4;
            _state = DATA_RECEIVER_STATE_RECEIVING;
            return DATA_AVAILABLE;
        }
    }
    break;

    case DATA_RECEIVER_STATE_RECEIVING:
    {
        int dataByte = read();

        if(dataByte < 256)
        {
            if(size == 0)
            {
                if(dataByte == 0)
                {
                    data[len] = 0;
                    total = len;
                    len = 0;
                    _state = DATA_RECEIVER_STATE_NONE;
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
                data[len] = dataByte;
                len++;

                xorValue = xorValue ^ dataByte;

                size--;

                if(size == 0)
                {
                    Serial.write(xorValue);
                }
            }
            return DATA_RECEIVING;
        }
        else
        {
            return DATA_ERROR;
        }
    }
    break;

    default:
        return DATA_ERROR;
    }

    return DATA_NONE;
}

