#include "ColorTools.h"

#define SCREEN_PIXELS 50

struct PointRGB
{
  int r;
  int g;
  int b;
  
  float pos;
  float vel;
  float size;
  
  void init(int r, int g, int b, float pos, float vel, float size)
  {
    this->r = r;
    this->g = g;
    this->b = b;
    this->pos = pos;
    this->vel = vel;
    this->size = size;
    
  }
  
  float getAlpha(float pos)
  {
    float d = pos - this->pos;

    if(d > size || d < -size)
    {
      return 0;
    }

    return (float)((cos((d / size) * 3.1415962f) + 1.0f) * 0.5f);
  }
};

struct Screen
{
  int pixels[SCREEN_PIXELS];
  
  void clear(int color)
  {
    for(int i = 0; i < SCREEN_PIXELS; i++)
    {
      pixels[i] = color;
    }
  }
  
  void fade(int value)
  {
    for(int i = 0; i < SCREEN_PIXELS; i++)
    {
      pixels[i] = fadeValue(pixels[i], value);
    }
  }
  
  void blendAdd(int i, int a, int r, int g, int b)
  {
    int c = pixels[i];

    b = ::blendAdd(a, b, c & 0xff);
    c = c >> 8;
    
    g = ::blendAdd(a, g, c & 0xff);
    c = c >> 8;

    r = ::blendAdd(a, r, c & 0xff);

    pixels[i] = (0xff << 24) | (r << 16) | (g << 8) | b;
  }
  
  void renderPoint(PointRGB *p)
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
};



int led = 13;

Screen screen;

void setup()
{
  pinMode(led, OUTPUT);     
}

void loop()
{
  screen.clear(0);
  
  screen.renderPoint(0);
  
  digitalWrite(led, HIGH);
  delay(1000);
  digitalWrite(led, LOW);
  delay(1000);
}
