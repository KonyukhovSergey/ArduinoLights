#ifndef LIGHT_MACHINE_H
#define LIGHT_MACHINE_H

#define MACHINE_EXEC_FINISH         0x01
#define MACHINE_EXEC_ERROR          0x02
#define MACHINE_EXEC_INTERRUPT      0x03

#define MACHINE_INTERRUPT_MILLIS    250

#define EEPROM_SIZE 1024

#define STACK_SIZE	32
#define MAX_VARIABLES_COUNT 128

#include <math.h>
#include <Arduino.h>
#include <EEPROM.h>
#include <SerialClass.h>

#include "Screen.h"

extern Screen screen;

enum CommandTypes
{
    ERROR_,

    ADD, SUB, MUL, DIV, NEG,

    SIN, COS, EXP, SQRT, POW, ABS,

    LOOP__, DELAY, TIME, RND, RET, END__,

    GREATER, LOWER, EQ, NEQ,

    SET_RGB, SET_GAMMA,

    PUSH_BYTE, PUSH_SHORT, PUSH_INT, PUSH_FLOAT,

    SHLEFT, SHRIGHT,

    GET_R, GET_G, GET_B,

    MEM_SET, MEM_GET,

    MOD,

    SEND,
};

struct LightMachine
{
    uint16_t pos;

    float *stackf;
    uint32_t *stacki;
    float *variables;

    uint8_t stackPosition;

    void init(uint8_t *variables, uint8_t *stack)
    {
        this->variables = (float*) variables;
        this->stackf = (float*) stack;
        this->stacki = (uint32_t*) stack;

        stackPosition = 0;

        screen.clear(0, 0, 0);

        for(int i = 0; i < MAX_VARIABLES_COUNT; i++)
        {
            variables[i] = 0;
        }

        pos = 0;
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

    uint8_t execute()
    {
        uint32_t executionStart = millis();

        while(millis() - executionStart < MACHINE_INTERRUPT_MILLIS)
        {
            uint8_t b = prog(pos);

            pos++;

            if(b == 0x00)
            {
                return MACHINE_EXEC_FINISH;
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
                    screen.shleft();
                    break;

                case SHRIGHT:
                    screen.shright();
                    break;

                case SET_RGB:
                {
                    uint8_t b = pop();
                    uint8_t g = pop();
                    uint8_t r = pop();
                    uint8_t i = pop();

                    screen.set(i, r, g, b);
                }
                break;

                case SET_GAMMA:
                    screen.setGamma(pop());
                    break;

                case GET_R:
                    push(screen.r(pop()));
                    break;

                case GET_G:
                    push(screen.g(pop()));
                    break;

                case GET_B:
                    push(screen.b(pop()));
                    break;

                case RND:
                    push((float)random(32767)/32767.0f);
                    break;

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

                case SEND:
                    screen.send();
                    break;

                default:
                    return MACHINE_EXEC_ERROR;
                }
                break;

            case 1: // some call form
            {
                uint16_t address = ((b & 0x0f) << 8) | prog(pos);

                if(address >= EEPROM_SIZE)
                {
                    return MACHINE_EXEC_ERROR;
                }

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
        return MACHINE_EXEC_INTERRUPT;
    }
};

#endif


















