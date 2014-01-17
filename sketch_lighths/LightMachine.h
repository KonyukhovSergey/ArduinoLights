#ifndef LIGHT_MACHINE_H
#define LIGHT_MACHINE_H

#define STACK_SIZE	48
#define MAX_VARIABLES_COUNT 64

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

  SET_RGB, SET_COLOR,

  PUSH_BYTE, PUSH_SHORT, PUSH_INT, PUSH_FLOAT,
};

struct LightMachine
{
  uint16_t pos;

  float stackValues[STACK_SIZE];
  uint8_t prog[1024];

  uint32_t *stackInts;
  uint8_t stackPosition;
  uint8_t interuptCounter;
  uint16_t startPosition;

  Screen *screen;

  float variables[MAX_VARIABLES_COUNT];

  void init(Screen *screen)
  {
    this->screen = screen;
    screen->clear(0, 0, 0);

    startPosition = 0;

    stackPosition = 0;
    stackInts = (uint32_t*)stackValues;

    for(int i = 0; i < MAX_VARIABLES_COUNT; i++)
    {
      variables[i] = 0;
    }

    for(int i = 0; i < 1024; i ++)
    {
      prog[i] = EEPROM.read(i);
    }

    interuptCounter = 0;

    startPosition = execute();
  }	

  float pop()
  {
    if(stackPosition > 0)
    {
      stackPosition--;
      return stackValues[stackPosition];
    }
  }

  void push(float value)
  {
    if(stackPosition < STACK_SIZE - 1)
    {
      stackValues[stackPosition] = value;
      stackPosition++;
    }
  }

  void pushInt(uint8_t value)
  {
    stackInts[stackPosition] = value;
    stackPosition++;
  }

  uint8_t popInt()
  {
    stackPosition--;
    return stackInts[stackPosition];
  }

  float getByte()
  {
    uint8_t value;
    value = prog[pos];
    pos += 1;
    return value;
  }

  float getShort()
  {
    uint16_t value;
    uint8_t *ptr = (uint8_t*) & value;

    *(ptr + 0) = prog[pos + 1];
    *(ptr + 1) = prog[pos + 0];

    pos += 2;  
  }

  float getInt()
  {
    uint32_t value;
    uint8_t *ptr = (uint8_t*)&value;

    *(ptr + 0) = prog[pos + 3];
    *(ptr + 1) = prog[pos + 2];
    *(ptr + 2) = prog[pos + 1];
    *(ptr + 3) = prog[pos + 0];

    pos += 4;

    return value;
  }

  float getFloat()
  {
    float value;
    uint8_t *ptr = (uint8_t*)&value;

    *(ptr + 0) = prog[pos + 3];
    *(ptr + 1) = prog[pos + 2];
    *(ptr + 2) = prog[pos + 1];
    *(ptr + 3) = prog[pos + 0];

    pos += 4;

    return value;
  }


  uint16_t execute()
  {
    pos = startPosition;

    while(true)
    {
      interuptCounter++;

      if(interuptCounter > 254)
      {
        interuptCounter = 0;

        if(Serial.available() > 0)
        {
          return 65535;
        }
      }

      //uint8_t b = EEPROM.read(pos);
      uint8_t b = prog[pos];

      if(b == 0x00)
      {
        break;
      }

      pos++;

      if(b < 64)
      {
        switch(b)
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
            //        	stackInts[stackPosition-2] = stackInts[stackPosition-2] > stackInts[stackPosition-1] ? 1 : 0;
            //        	stackPosition--;
            float rv = pop();
            float lv = pop();
            pushInt(lv > rv? 1 : 0);
          }
          break;

        case LOWER:
          {
            //        	stackInts[stackPosition-2] = stackInts[stackPosition-2] < stackInts[stackPosition-1] ? 1 : 0;
            //        	stackPosition--;
            float rv = pop();
            float lv = pop();
            pushInt(lv < rv? 1 : 0);
          }
          break;

        case EQ:
          {
            float rv = pop();
            float lv = pop();
            pushInt( fabs(lv - rv) < 0.00001 ? 1 : 0);
          }
          break;

        case NEQ:
          {
            float rv = pop();
            float lv = pop();
            pushInt( fabs(lv - rv) > 0.00001 ? 1 : 0);
          }
          break;

        case ADD:
          {
            //        	stackValues[stackPosition-2] = stackValues[stackPosition-2] + stackValues[stackPosition-1];
            //        	stackPosition--;
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


        case SET_RGB:
          {
            uint8_t b = pop();
            uint8_t g = pop();
            uint8_t r = pop();
            uint8_t i = pop();

            screen->set(i, r, g, b);
          }
          break;

        case RND:
          push((float)random(999999)/999999.0f);
          break;

        case END:
          return pos;

        default:
          //TODO: error
          return pos;
          break;
        }
        continue;
      }

      if(b & 0x80) // push or pop
      {
        if(b & 0x40) // pop
        {
          variables[b & 0x3f] = pop();
          continue;
        }
        else // push
        {
          push(variables[b & 0x3f]);
          continue;
        }
      }
      else // any jump command
      {
        uint16_t address = ((b & 0xf) << 8) | prog[pos];
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
    }

    return pos;
  }
};

#endif






