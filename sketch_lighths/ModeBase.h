#ifndef MODE_BASE_H
#define MODE_BASE_H

#include <Arduino.h>
#include <EEPROM.h>

#include "Screen.h"

extern uint8_t data[256];

typedef uint8_t(*drawFunction)(Screen&);

#endif
