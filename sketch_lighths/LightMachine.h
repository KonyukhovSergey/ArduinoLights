#ifndef LIGHT_MACHINE_H
#define LIGHT_MACHINE_H

#define STACK_SIZE	32

#include <math.h>
#include <Arduino.h>
#include <EEPROM.h>
#include <SerialClass.h>

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
#define COMMAND_ABS	0x0f
#define COMMAND_CALL	0x10
#define COMMAND_RET	0x11
#define COMMAND_JUMP	0x12
#define COMMAND_JUMPZ	0x13
#define COMMAND_END	0x14

#define COMMAND_GREATER	0x15
#define COMMAND_LOWER	0x16

struct LightMachine
{
  uint16_t len;
  uint16_t pos;

  float stackValues[STACK_SIZE];
  uint32_t *stackInts;
  uint8_t stackPosition;
  uint8_t interuptCounter;

  float variables[26];

  void init(uint16_t lenght)
  {
    len = lenght + BYTE_CODE_OFFSET;

    stackPosition = 0;
    stackInts = (uint32_t*)stackValues;

    for(int i = 0; i < 26; i++)
    {
      variables[i] = 0;
    }

    interuptCounter = 0;
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
      interuptCounter++;

      if(interuptCounter > 254)
      {
        interuptCounter = 0;

        if(Serial.available() > 0)
        {
          return 65535;
        }
      }

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
      case COMMAND_GREATER:
        {
          float rv = pop();
          float lv = pop();
          pushInt(lv > rv? 1 : 0);
        }
        break;

      case COMMAND_LOWER:
        {
          float rv = pop();
          float lv = pop();
          pushInt(lv < rv? 1 : 0);
        }
        break;

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
        break;
      case COMMAND_ABS:
        {
          float value = pop();
          push(value < 0 ? -value : value);
        }
        break;
      case COMMAND_CALL:
        {
          uint32_t address = popInt() + BYTE_CODE_OFFSET;
          pushInt(pos);
          pos = address;
        }
        break;

      case COMMAND_RET:
        {
          pos = popInt();
        }
        break;

      case COMMAND_JUMP:
        {
          pos = popInt() + BYTE_CODE_OFFSET;
        }
        break;

      case COMMAND_JUMPZ:
        {
          uint32_t address = popInt() + BYTE_CODE_OFFSET;

          if(popInt() == 0)
          {
            pos = address;
          }
        }
        break;

      case COMMAND_END:
        return pos - BYTE_CODE_OFFSET;
      }
    }

    return pos - BYTE_CODE_OFFSET;
  }
};

#endif



