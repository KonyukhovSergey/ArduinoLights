#include <SPI.h>
#include <EEPROM.h>

#include "DataReceiver.h"
#include "LightMachine.h"
#include "Screen.h"


#define STATE_INIT 0x01
#define STATE_EXEC 0x02
#define STATE_STOP 0x03
#define STATE_RECV 0x04

uint8_t data[MAX_VARIABLES_COUNT * 4 + STACK_SIZE * 4 + 256 + (SCREEN_PIXELS * 4)];

#define DATA_OFFSET_VARIABLES 0
#define DATA_OFFSET_STACK (MAX_VARIABLES_COUNT * 4)
#define DATA_OFFSET_GAMMA (MAX_VARIABLES_COUNT * 4 + STACK_SIZE * 4)
#define DATA_OFFSET_PIXELS (MAX_VARIABLES_COUNT * 4 + STACK_SIZE * 4 + 256)

DataReceiver dataReceiver;
LightMachine lightMachine;
Screen screen;

uint8_t state;

void setup()
{
    Serial.begin(9600);
    SPI.begin();
    SPI.setBitOrder(MSBFIRST);
    SPI.setDataMode(SPI_MODE0);
    SPI.setClockDivider(SPI_CLOCK_DIV16);
    delay(1);
    randomSeed(analogRead(0));

    state = STATE_INIT;
}

void init()
{
    dataReceiver.init(data);
    screen.init(data + DATA_OFFSET_PIXELS, data + DATA_OFFSET_GAMMA);
    lightMachine.init(data + DATA_OFFSET_VARIABLES, data + DATA_OFFSET_STACK);
    state = STATE_EXEC;
}

void exec()
{
    switch(lightMachine.execute())
    {
    case MACHINE_EXEC_ERROR:
        screen.clear(255, 0, 0);
        screen.send();
        state = STATE_STOP;
        break;

    case MACHINE_EXEC_INTERRUPT:
        if(dataReceiver.state() == DATA_AVAILABLE)
        {
            state = STATE_RECV;
        }
        break;

    case MACHINE_EXEC_FINISH:
        screen.clear(255, 255, 255);
        screen.send();
        state = STATE_STOP;
        break;
    }
}

void recv()
{
    switch(dataReceiver.state())
    {
    case DATA_RECEIVING:
        screen.progress(dataReceiver.len, dataReceiver.total);
        screen.send();
        delay(10);
        break;

    case DATA_READY:
    {
        for(uint16_t i = 0; i < dataReceiver.total; i++)
        {
            EEPROM.write(i, dataReceiver.data[i]);
            screen.set(i * 50 / dataReceiver.total, 0, 0, 255);
            screen.send();
        }
        state = STATE_INIT;
    }
    break;

    case DATA_ERROR:
        screen.clear(255, 0, 0);
        screen.send();
        delay(500);
        screen.clear(0, 255, 0);
        screen.send();
        delay(500);
        screen.clear(0, 0, 255);
        screen.send();
        delay(500);
        state = STATE_INIT;
        break;
    }
}

void stop()
{
    if(dataReceiver.state() == DATA_AVAILABLE)
    {
        state = STATE_RECV;
    }
    delay(100);
}

void loop()
{
    switch(state)
    {
    case 0x01:
        init();
        break;

    case STATE_EXEC:
        exec();
        break;

    case STATE_RECV:
        recv();
        break;

    case STATE_STOP:
        stop();
        break;
    }
}



