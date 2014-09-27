#include <Arduino.h>

void delay(int value)
{
    Sleep(value);
}

int analogRead(int pin)
{
    return 0;
}

void randomSeed(int value)
{
}

int random(int maxValue)
{
    return rand() % maxValue;
}

int millis()
{
    return GetTickCount();
}
