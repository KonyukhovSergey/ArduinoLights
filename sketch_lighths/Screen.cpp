#include "Screen.h"
#include "utils.h"

void Screen::clear(int color)
{
  for(int i = 0; i < SCREEN_PIXELS; i++)
  {
    pixels[i].fromInt(color);
  }
}

void Screen::fade(int value)
{
  for(int i = 0; i < SCREEN_PIXELS; i++    )
  {
    pixels[i].r = fadeValue(pixels[i].r, value);
    pixels[i].g = fadeValue(pixels[i].g, value);
    pixels[i].b = fadeValue(pixels[i].b, value);
  }
}

void Screen::blendAdd(int i, int a, int r, int g, int b)
{
  pixels[i].r = ::blendAdd(a, r, pixels[i].r);
  pixels[i].g = ::blendAdd(a, g, pixels[i].g);
  pixels[i].b = ::blendAdd(a, b, pixels[i].b);
}

void Screen::renderPoint(PointRGB *p)
{
  for(int i = 0; i < SCREEN_PIXELS; i++)
  {
    int a = (int)(255 * p->getAlpha(i));

    if(a > 0)
    {
      blendAdd(i, a, p->r, p->g, p->b);
    }
  }
}

void Screen::set(uint8_t i, uint8_t r, uint8_t g, uint8_t b)
{
  if(i >= 0 && i < SCREEN_PIXELS)
  {
    pixels[i].r = r;
    pixels[i].g = g;
    pixels[i].b = b;
  }
}

void Screen::sendToModules()
{
  for(int i = 0; i < SCREEN_PIXELS; i++) 
  {
    SPI.transfer(pixels[i].r);
    SPI.transfer(pixels[i].g);
    SPI.transfer(pixels[i].b);
  }
  delay(1);
}


