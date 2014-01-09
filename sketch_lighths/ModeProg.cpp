#include "ModeProg.h"

LightMachine lm;

uint8_t drawProg(Screen &screen)
{
  return ((ModeProg*) data)->draw(screen);
}

