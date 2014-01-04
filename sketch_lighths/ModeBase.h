#ifndef MODE_BASE_H
#define MODE_BASE_H

#include <Arduino.h>

#include "Screen.h"

extern uint8_t data[256];

typedef void(*drawFunction)(Screen&);

#endif
