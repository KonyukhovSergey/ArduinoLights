// PointRGB Copyright (c) Sergey Konyukhov. All right reserved

#ifndef POINTRGB_H
#define POINTRGB_H

class PointRGB
{
	private:
		int r;
		int g;
		int b;
  
		float pos;
		float vel;
		float size;
	
		float getAlpha(float pos);
		void init(int r, int g, int b, float pos, float vel, float size);
		
	public:
		void init(float size, float sizeDelta);
		void tick(float dt);

};

#endif
 