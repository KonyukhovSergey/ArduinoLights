#ifndef LIGHT_MACHINE_H
#define LIGHT_MACHINE_H

#define EEPROM_SIZE 1024

#define STACK_SIZE	32
#define MAX_VARIABLES_COUNT 128

#include <math.h>
#include <Arduino.h>
#include <EEPROM.h>
#include <SerialClass.h>

#include "Screen.h"

enum CommandTypes
{
  ERROR_,

  ADD, SUB, MUL, DIV, NEG,

  SIN, COS, EXP, SQRT, POW, ABS,

  LOOP, DELAY, TIME, RND, RET, END,

  GREATER, LOWER, EQ, NEQ,

  SET_RGB, SET_GAMMA, 

  PUSH_BYTE, PUSH_SHORT, PUSH_INT, PUSH_FLOAT,

  SHLEFT, SHRIGHT,

  GET_R, GET_G, GET_B,

  MEM_SET, MEM_GET,

  MOD,
};

struct LightMachine
{
  uint16_t pos;

  float stackf[STACK_SIZE];
  uint32_t *stacki;
  float variables[MAX_VARIABLES_COUNT];

  uint8_t stackPosition;
  uint16_t loopPosition;

  Screen *screen;

  void init(Screen *screen)
  {
    this->screen = screen;

    screen->clear(0, 0, 0);

    loopPosition = 0;

    stackPosition = 0;
    stacki = (uint32_t*)stackf;

    for(int i = 0; i < MAX_VARIABLES_COUNT; i++)
    {
      variables[i] = 0;
    }

    loopPosition = execute();
  }

  void stop()
  {
    loopPosition = EEPROM_SIZE;
  }

  uint8_t prog(uint16_t pos)
  {
    return EEPROM.read(pos);
  }

  float pop()
  {
    stackPosition--;
    return stackf[stackPosition];
  }

  void push(float value)
  {
    stackf[stackPosition] = value;
    stackPosition++;
  }

  void pushInt(uint32_t value)
  {
    stacki[stackPosition] = value;
    stackPosition++;
  }

  uint32_t popInt()
  {
    stackPosition--;
    return stacki[stackPosition];
  }

  float getByte()
  {
    uint8_t value;
    value = prog(pos);
    pos += 1;
    return value;
  }

  float getShort()
  {
    uint16_t value;
    uint8_t *ptr = (uint8_t*) & value;

    *(ptr + 0) = prog(pos + 1);
    *(ptr + 1) = prog(pos + 0);

    pos += 2;  

    return value;
  }

  float getInt()
  {
    uint32_t value;
    uint8_t *ptr = (uint8_t*)&value;

    *(ptr + 0) = prog(pos + 3);
    *(ptr + 1) = prog(pos + 2);
    *(ptr + 2) = prog(pos + 1);
    *(ptr + 3) = prog(pos + 0);

    pos += 4;

    return value;
  }

  float getFloat()
  {
    float value;
    uint8_t *ptr = (uint8_t*)&value;

    *(ptr + 0) = prog(pos + 3);
    *(ptr + 1) = prog(pos + 2);
    *(ptr + 2) = prog(pos + 1);
    *(ptr + 3) = prog(pos + 0);

    pos += 4;

    return value;
  }

  uint16_t execute()
  {
    if(loopPosition >= EEPROM_SIZE)
    {
      return EEPROM_SIZE;
    }

    pos = loopPosition;

    while(true)
    {
      uint8_t b = prog(pos);
      pos++;

      if(b == 0x00)
      {
        loopPosition = EEPROM_SIZE;
        return EEPROM_SIZE;
      }

      switch(b >> 6)
      {
      case 0: // command

        switch(b & 0x3f)
        {
        case PUSH_BYTE:
          push(getByte());
          break;

        case PUSH_SHORT:
          push(getShort());
          break;

        case PUSH_INT:
          push(getInt());
          break;

        case PUSH_FLOAT:
          push(getFloat());
          break;

        case GREATER:
          {
            //stackInts[stackPosition-2] = stackInts[stackPosition-2] > stackInts[stackPosition-1] ? 1 : 0;
            //stackPosition--;
            float rv = pop();
            float lv = pop();
            pushInt(lv > rv ? 1 : 0);
          }
          break;

        case LOWER:
          {
            //stackInts[stackPosition-2] = stackInts[stackPosition-2] < stackInts[stackPosition-1] ? 1 : 0;
            //stackPosition--;
            float rv = pop();
            float lv = pop();
            pushInt(lv < rv ? 1 : 0);
          }
          break;

        case EQ:
          {
            float rv = pop();
            float lv = pop();
            pushInt(fabs(lv - rv) < 0.00001 ? 1 : 0);
          }
          break;

        case NEQ:
          {
            float rv = pop();
            float lv = pop();
            pushInt(fabs(lv - rv) > 0.00001 ? 1 : 0);
          }
          break;

        case ADD:
          {
            //stackValues[stackPosition-2] = stackValues[stackPosition-2] + stackValues[stackPosition-1];
            //stackPosition--;
            float rv = pop();
            float lv = pop();
            push(lv + rv);
          }
          break;

        case SUB:
          {
            float rv = pop();
            float lv = pop();
            push(lv - rv);
          }
          break;

        case MUL:
          {
            float rv = pop();
            float lv = pop();
            push(lv * rv);
          }
          break;

        case DIV:
          {
            float rv = pop();
            float lv = pop();
            push(lv / rv);
          }
          break;

        case NEG:
          {
            push(-pop());
          }
          break;

        case SIN:
          {
            push(sin(pop()));
          }
          break;

        case COS:
          {
            push(cos(pop()));
          }
          break;

        case EXP:
          {
            push(exp(pop()));
          }
          break;

        case LOOP:
          {
            return pos;
          }

        case SQRT:
          {
            push(sqrt(pop()));
          }
          break;

        case DELAY:
          {
            delay(pop());
          }
          break;

        case TIME:
          {
            push(millis());
          }
          break;

        case POW:
          {
            float rv = pop();
            float lv = pop();
            push(pow(lv, rv));
          }
          break;
        case ABS:
          {
            float value = pop();
            push(value < 0 ? -value : value);
          }
          break;

        case RET:
          {
            pos = popInt();
          }
          break;

        case SHLEFT:
          screen->shleft();
          break;

        case SHRIGHT:
          screen->shright();
          break;

        case SET_RGB:
          {
            uint8_t b = pop();
            uint8_t g = pop();
            uint8_t r = pop();
            uint8_t i = pop();

            screen->set(i, r, g, b);
          }
          break;

        case SET_GAMMA:
          screen->setGamma(pop());
          break;

        case GET_R:
          push(screen->r(pop()));
          break;        	

        case GET_G:
          push(screen->g(pop()));
          break;        	

        case GET_B:
          push(screen->b(pop()));
          break;        	

        case RND:
          push((float)random(32767)/32767.0f);
          break;

        case END:
          return pos;

        case MEM_SET:
          {
            float value = pop();
            uint8_t adderss = (uint8_t)pop() + getByte();
            variables[adderss] = value;
          }
          break;

        case MEM_GET:
          {
            uint8_t adderss = (uint8_t)pop() + getByte();
            push(variables[adderss]);
          }
          break;

        case MOD:
          {
            float value = pop();
            push(value - ((uint32_t)value));
          }
          break;

        default:
          stop();
          return pos;
          break;
        }
        break;

      case 1: // some call form
        {
          uint16_t address = ((b & 0x0f) << 8) | prog(pos);

          pos++;

          switch(b >> 4)
          {
          case 4: // call
            pushInt(pos);
            pos = address;
            break;

          case 5: // goto
            pos = address;
            break;

          case 6: // jumpz
            if(popInt() == 0)
            {
              pos = address;
            }
            break;
          }
        }
        break;

      case 2: // push variable
        push(variables[b & 0x3f]);
        break;

      case 3: // pop veriable
        variables[b & 0x3f] = pop();
        break;
      }
    }
    return pos;
  }
};

#endif


















