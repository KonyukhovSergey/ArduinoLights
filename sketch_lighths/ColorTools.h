#ifndef COLOR_TOOLS_H
#define COLOR_TOOLS_H

int HSV2RGB(float h, float s, float v);

int blendAdd(int a, int s, int d);
int fadeValue(int v, int c);

#endif
