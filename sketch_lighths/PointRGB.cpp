#include <Arduino.h>
#include "PointRGB.h"

void PointRGB::init(uint8_t r, uint8_t g, uint8_t b, float pos, float vel, float size)
{
  this->r = r;
  this->g = g;
  this->b = b;
  this->pos = pos;
  this->vel = vel;
  this->size = size;
}

float PointRGB::getAlpha(float pos)
{
  float d = pos - this->pos;

  if(d > size || d < -size)
  {
    return 0;
  }

  return (float)((cos((d / size) * 3.1415962f) + 1.0f) * 0.5f);
}


