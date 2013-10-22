#include "ColorTools.h"

int HSV2RGB(float h, float s, float v)
{
	int i, f, r, g, b;
	float p, q, t;

	h = max(0.0, min(360.0, h));
	s = max(0.0, min(100.0, s));
	v = max(0.0, min(100.0, v));
	
	s /= 100;
	v /= 100;

	if(s == 0)
	{
		*r = *g = *b = round(v * 255);
		return;
	}
 
	h /= 60;
	i = floor(h);
	f = h - i;
	p = v * (1 - s);
	q = v * (1 - s * f);
	t = v * (1 - s * (1 - f));

	switch(i)
	{
	case 0:
		r = round(255 * v);
		g = round(255 * t);
		b = round(255 * p);
	break;

	case 1:
		r = round(255 * q);
		g = round(255 * v);
		b = round(255 * p);
	break;
	
	case 2:
		r = round(255 * p);
		g = round(255 * v);
		b = round(255 * t);
	break;
	
	case 3:
		r = round(255 * p);
		g = round(255 * q);
		b = round(255 * v);
	break;
	
	case 4:
		r = round(255 * t);
		g = round(255 * p);
		b = round(255 * v);
	break;
	
	default: // case 5:
		r = round(255 * v);
		g = round(255 * p);
		b = round(255 * q);
	}
	
	return (r << 16) | (g << 8) | b;
}

int blendAdd(int a, int s, int d)
{
  int v = ((a * s) >> 8) + d;
  
  if(v < 0)
  {
    v = 0;
  }
  
  if(v > 255)
  {
    v = 255;
  }
  
  return v;
}

int fadeValue(int v, int c)
{
  if(v >= c)
  {
    v -= c;

    if(v < 0)
    {
      v = 0;
    }
  }
  
  return v;
}

