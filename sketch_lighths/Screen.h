#ifndef SCREEN_H
#define SCREEN_H

#include <Arduino.h>
#include <SPI.h>

#define SCREEN_PIXELS 50

struct Screen
{
  uint8_t pixels[SCREEN_PIXELS * 3];

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
  
  void send()
  {
	for(int i = 0; i < SCREEN_PIXELS * 3; i += 3) 
    {
      SPI.transfer(pixels[i + 0]);
      SPI.transfer(pixels[i + 1]);
      SPI.transfer(pixels[i + 2]);
    }
    delay(1);
  }
};

#endif

