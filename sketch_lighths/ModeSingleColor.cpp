#include "ModeSingleColor.h"

uint8_t drawSingleColor(Screen &screen)
{
  return ((ModeSingleColor*) data)->draw(screen);
}


