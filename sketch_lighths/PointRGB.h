#ifndef POINTRGB_H
#define POINTRGB_H

struct PointRGB
{
  uint8_t r;
  uint8_t g;
  uint8_t b;

  float pos;
  float vel;
  float size;

  void init(uint8_t r, uint8_t g, uint8_t b, float pos, float vel, float size);
  float getAlpha(float pos);
};

#endif

