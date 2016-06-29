#include <EEPROM.h>

#include "DataReceiver.h"
#include "LightMachine.h"
#include "Screen2812.h"

#define STATE_INIT 0x01
#define STATE_EXEC 0x02
#define STATE_STOP 0x03
#define STATE_RECV 0x04

uint8_t data[MAX_VARIABLES_COUNT * 4 + STACK_SIZE * 4 + 256 + (SCREEN_PIXELS * 0)];

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
    delay(1);
    randomSeed(analogRead(0));
    state = STATE_INIT;
}

void stateInit()
{
    dataReceiver.init(data);
    screen.init(data + DATA_OFFSET_PIXELS, data + DATA_OFFSET_GAMMA);
    lightMachine.init(data + DATA_OFFSET_VARIABLES, data + DATA_OFFSET_STACK);
    state = STATE_EXEC;
}

void stateExec()
{
    switch(lightMachine.execute())
    {
    case MACHINE_EXEC_ERROR:
        screen.clear(64, 0, 0); screen.send(); delay(250);
        screen.clear(255, 0, 0); screen.send(); delay(250);
        screen.clear(64, 0, 0); screen.send(); delay(250);
        screen.clear(255, 0, 0); screen.send(); delay(250);
        state = STATE_STOP;
        break;

    case MACHINE_EXEC_INTERRUPT:
        if(dataReceiver.state() == DATA_AVAILABLE)
        {
            state = STATE_RECV;
        }
        break;

    case MACHINE_EXEC_FINISH:
        state = STATE_STOP;
        break;
    }
}

void stateRecv()
{
    switch(dataReceiver.state())
    {
    case DATA_RECEIVING:
        screen.progress(dataReceiver.len, dataReceiver.total);
        delay(2);
        break;

    case DATA_READY:
    {
        for(uint16_t i = 0; i < dataReceiver.total; i++)
        {
            EEPROM.write(i, dataReceiver.data[i]);
        }
        state = STATE_INIT;
    }
    break;

    case DATA_ERROR:
        screen.clear(255, 0, 0); screen.send(); delay(250);
        screen.clear(0, 255, 0); screen.send(); delay(250);
        screen.clear(0, 0, 255); screen.send(); delay(250);
        state = STATE_INIT;
        break;
    }
}

void stateStop()
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
    case STATE_INIT:
        stateInit();
        break;

    case STATE_EXEC:
        stateExec();
        break;

    case STATE_RECV:
        stateRecv();
        break;

    case STATE_STOP:
        stateStop();
        break;
    }
}





