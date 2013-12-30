#ifndef MODE_POINTS_RGB_H
#define MODE_POINTS_RGB_H

#include <Arduino.h>

#include "Screen.h"
#include "utils.h"
#include "ARGB.h"
#include "PointRGB.h"

struct ModePointsRGB
{
	float t;
	PointRGB p;
	PointRGB pr;
	PointRGB pg;
	PointRGB pb;

	void init()
	{
		p.init(255, 255, 255, 25, 0, 8);
		pr.init(255,0,0, 0,0,10);
		pg.init(0,255,0, 0,0,10);
		pb.init(0,0,255, 0,0,10);
		t = 0;
	}

	void draw(Screen &screen)
	{
		t += 0.0025f;

		screen.clear(0);

		p.pos = 25 + 25 * sin(t*10.0f);
		screen.renderPoint(&p);

		pr.pos = 25 + 25 * sin(t * 2.0f + 0.0f);
		screen.renderPoint(&pr);

		pg.pos = 25 + 25 * sin(t * 3.0f + 1.0f);
		screen.renderPoint(&pg);

		pb.pos = 25 + 25 * sin(t * 5.0f + 2.0f);
		screen.renderPoint(&pb);
	}

};

#endif