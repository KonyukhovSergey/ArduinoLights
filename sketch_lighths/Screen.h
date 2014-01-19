#ifndef SCREEN_H
#define SCREEN_H

#include <Arduino.h>
#include <SPI.h>

#define SCREEN_PIXELS 50

struct Screen
{
  uint8_t *pixels;

  void init(uint8_t *pixels)
  {
    this->pixels = pixels;
    clear(0, 0, 0);
  }

  void clear(uint8_t r, uint8_t g, uint8_t b)
  {
    for(int i = 0; i < SCREEN_PIXELS * 3; i += 3)
    {
      pixels[i + 0] = r;
      pixels[i + 1] = g;
      pixels[i + 2] = b;
    }
  }

  uint8_t r(uint8_t i)
  {
    return pixels[i * 3 + 0];
  }

  uint8_t g(uint8_t i)
  {
    return pixels[i * 3 + 1];
  }

  uint8_t b(uint8_t i)
  {
    return pixels[i * 3 + 2];
  }

  void set(uint8_t i, uint8_t r, uint8_t g, uint8_t b)
  {
    i *= 3;
    pixels[i + 0] = r;
    pixels[i + 1] = g;
    pixels[i + 2] = b;
  }

  void shleft()
  {
    uint8_t r, g, b;

    r = pixels[0];
    g = pixels[1];
    b = pixels[2];

    uint8_t i;

    for(i = 0; i < SCREEN_PIXELS * 3 - 3; i += 3)
    {
      pixels[i + 0] = pixels[i + 3];
      pixels[i + 1] = pixels[i + 4];
      pixels[i + 2] = pixels[i + 5];
    }

    pixels[i + 0] = r;
    pixels[i + 1] = g;
    pixels[i + 2] = b;
  }

  void shright()
  {
    uint8_t r, g, b;

    r = pixels[SCREEN_PIXELS * 3 - 3];
    g = pixels[SCREEN_PIXELS * 3 - 2];
    b = pixels[SCREEN_PIXELS * 3 - 1];

    uint8_t i;

    for(i = SCREEN_PIXELS * 3 - 3; i >= 3; i -= 3)
    {
      pixels[i + 0] = pixels[i - 3];
      pixels[i + 1] = pixels[i - 2];
      pixels[i + 2] = pixels[i - 1];
    }

    pixels[i + 0] = r;
    pixels[i + 1] = g;
    pixels[i + 2] = b;
  }

  void send()
  {
    for(int i = 0; i < SCREEN_PIXELS * 3; i ++) 
    {
      SPI.transfer(pixels[i]);
    }
    delay(1);
  }
};

#endif






