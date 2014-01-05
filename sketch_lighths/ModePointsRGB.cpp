#include "ModePointsRGB.h"

void drawPointsRGB(Screen &screen)
{
  ModePointsRGB *mode = (ModePointsRGB*)data;

  mode->t += 0.0025f;

  screen.clear(0);

  mode->p.pos = 25 + 25 * sin(mode->t*10.0f);
  screen.renderPoint(&mode->p);

  mode->pr.pos = 25 + 25 * sin(mode->t * 2.0f + 0.0f);
  screen.renderPoint(&mode->pr);

  mode->pg.pos = 25 + 25 * sin(mode->t * 3.0f + 1.0f);
  screen.renderPoint(&mode->pg);

  mode->pb.pos = 25 + 25 * sin(mode->t * 5.0f + 2.0f);
  screen.renderPoint(&mode->pb);
}


