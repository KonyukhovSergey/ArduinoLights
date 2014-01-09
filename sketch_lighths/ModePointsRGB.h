#ifndef MODE_POINTS_RGB_H
#define MODE_POINTS_RGB_H

#include <Arduino.h>

#include "Screen.h"
#include "utils.h"
#include "ARGB.h"
#include "PointRGB.h"

#include "ModeBase.h"

uint8_t drawPointsRGB(Screen &screen);

struct ModePointsRGB
{
  float t;
  PointRGB p;
  PointRGB pr;
  PointRGB pg;
  PointRGB pb;

  drawFunction init()
  {

    p.init(255, 255, 255, 25, 0, 8);
    pr.init(255,0,0, 0,0,10);
    pg.init(0,255,0, 0,0,10);
    pb.init(0,0,255, 0,0,10);
    t = 0;

    return drawPointsRGB;
  }
};

#endif

