#ifndef SCREEN_H
#define SCREEN_H

#include <Arduino.h>
#include <SPI.h>

#define SCREEN_PIXELS 50

struct Screen
{
  uint8_t *pixels;
  uint8_t *gamma;

  void init(uint8_t *pixels, uint8_t *gamma)
  {
    this->pixels = pixels;
    this->gamma = gamma;
    clear(0, 0, 0);
    setGamma(3.0f);
  }

  void setGamma(float gammaValue)
  {
    for(uint16_t i = 0; i < 256; i++)
    {
      gamma[i] = 255.0f * pow((float)i / 255.0f, gammaValue);
    }
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

  uint8_t r(uint16_t i)
  {
    if(i < SCREEN_PIXELS)
    {
      return pixels[i * 3 + 0];
    }
    return 0;
  }

  uint8_t g(uint16_t i)
  {
    if(i < SCREEN_PIXELS)
    {
      return pixels[i * 3 + 1];
    }
    return 0;
  }

  uint8_t b(uint16_t i)
  {
    if(i < SCREEN_PIXELS)
    {
      return pixels[i * 3 + 2];
    }
    return 0;
  }

  void set(uint16_t i, uint8_t r, uint8_t g, uint8_t b)
  {
    if(i < SCREEN_PIXELS)
    {
      i *= 3;
      pixels[i + 0] = r;
      pixels[i + 1] = g;
      pixels[i + 2] = b;
    }
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
      SPI.transfer(gamma[pixels[i]]);
    }
#ifdef MACHINE_MOCK
SPI.draw();
#endif // MACHINE_MOCK
    delay(1);
  }

  void progress(uint16_t position, uint16_t total)
  {
    uint8_t completed = (position * SCREEN_PIXELS) / total;

    for(int i = 0; i < SCREEN_PIXELS; i++)
    {
	  if(i > completed)
	  {
        SPI.transfer(32);
        SPI.transfer(32);
        SPI.transfer(32);
	  }
	  else
	  {
        SPI.transfer(0);
        SPI.transfer(255);
        SPI.transfer(0);
      }
    }
#ifdef MACHINE_MOCK
SPI.draw();
#endif // MACHINE_MOCK
    delay(1);
  }
};

#endif







