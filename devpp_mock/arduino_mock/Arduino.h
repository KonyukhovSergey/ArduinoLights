#ifndef ARDUINO_H
#define ARDUINO_H

#include <windows.h>
#include <math.h>

typedef unsigned char uint8_t;
typedef unsigned short uint16_t;
typedef unsigned int uint32_t;

void delay(int value);
int random(int maxValue);
void randomSeed(int value);
int millis();

int analogRead(int pin);

#endif
