#ifndef SCREEN_H
#define SCREEN_H

#include <Arduino.h>
#include <SPI.h>

#include "ARGB.h"
#include "PointRGB.h"

#define SCREEN_PIXELS 50

struct Screen
{
  ARGB pixels[SCREEN_PIXELS];

  void clear(int color);
  void fade(int value);
  void blendAdd(int i, int a, int r, int g, int b);
  void renderPoint(PointRGB *p);
  void set(uint8_t i, uint8_t r, uint8_t g, uint8_t b);
  void sendToModules();
};

#endif

