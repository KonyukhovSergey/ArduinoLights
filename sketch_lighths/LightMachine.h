#ifndef LIGHT_MACHINE_H
#define LIGHT_MACHINE_H

#define STACK_SIZE	32

#include <math.h>
#include <Arduino.h>
#include <EEPROM.h>

#define BYTE_CODE_OFFSET 3

#define COMMAND_ADD	0x01
#define COMMAND_SUB	0x02
#define COMMAND_MUL	0x03
#define COMMAND_DIV	0x04
#define COMMAND_NEG	0x05
#define COMMAND_SIN	0x06
#define COMMAND_COS	0x07
#define COMMAND_EXP	0x08
#define COMMAND_LOOP	0x09
#define COMMAND_SQRT 	0x0a
#define COMMAND_DELAY 	0x0b
#define COMMAND_TIME	0x0c
#define COMMAND_RND	0x0d
#define COMMAND_POW 	0x0e

struct LightMachine
{
  uint16_t len;
  uint16_t pos;

  float stackValues[STACK_SIZE];
  uint8_t stackPosition;

  float variables[26];

  void init(uint16_t lenght)
  {
    len = lenght + BYTE_CODE_OFFSET;

    stackPosition = 0;

    for(int i = 0; i < 26; i++)
    {
      variables[i] = 0;
    }
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
    if(stackPosition < STACK_SIZE)
    {
      stackValues[stackPosition] = value;
      stackPosition++;
    }
  }

  float getConst()
  {
    float value;
    uint8_t *ptr = (uint8_t*)&value;

    *(ptr + 0) = EEPROM.read(pos + 3);
    *(ptr + 1) = EEPROM.read(pos + 2);
    *(ptr + 2) = EEPROM.read(pos + 1);
    *(ptr + 3) = EEPROM.read(pos + 0);

    pos += 4;

    return value;
  }

  uint16_t execute(uint16_t startPosition)
  {
    pos = startPosition + BYTE_CODE_OFFSET;

    while(pos < len)
    {
      uint8_t b = EEPROM.read(pos);
      pos++;

      if(b == 0xc0)
      {
        push(getConst());
        continue;
      }

      if(b & 0x40)
      {
        push(variables[b & 0x1f]);
        continue;
      }

      if(b & 0x80)
      {
        variables[b & 0x1f] = pop();
        continue;
      }

      switch(b)
      {
      case COMMAND_ADD:
        {
          float rv = pop();
          float lv = pop();
          push(lv + rv);
        }
        break;

      case COMMAND_SUB:
        {
          float rv = pop();
          float lv = pop();
          push(lv - rv);
        }
        break;

      case COMMAND_MUL:
        {
          float rv = pop();
          float lv = pop();
          push(lv * rv);
        }
        break;

      case COMMAND_DIV:
        {
          float rv = pop();
          float lv = pop();
          push(lv / rv);
        }
        break;

      case COMMAND_NEG:
        {
          push(-pop());
        }
        break;

      case COMMAND_SIN:
        {
          push(sin(pop()));
        }
        break;

      case COMMAND_COS:
        {
          push(cos(pop()));
        }
        break;

      case COMMAND_EXP:
        {
          push(exp(pop()));
        }
        break;

      case COMMAND_LOOP:
        {
          return pos - BYTE_CODE_OFFSET;
        }

      case COMMAND_SQRT:
        {
          push(sqrt(pop()));
        }
        break;

      case COMMAND_DELAY:
        {
          delay(pop());
        }
        break;

      case COMMAND_TIME:
        {
          push(millis());
        }
        break;

      case COMMAND_POW:
        {
          float rv = pop();
          float lv = pop();
          push(pow(lv, rv));
        }
      }
    }

    return pos - BYTE_CODE_OFFSET;
  }
};

#endif
