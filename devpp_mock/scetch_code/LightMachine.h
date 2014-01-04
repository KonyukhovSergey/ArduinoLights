#ifndef LIGHT_MACHINE_H
#define LIGHT_MACHINE_H

#define STACK_SIZE	32

#include <math.h>
#include <Arduino.h>

struct LightMachine
{
	uint8_t *byteCode;
	uint8_t length;
	uint8_t position;
	
	float stackValues[STACK_SIZE];
	uint8_t stackPosition;
	
	float variables[26];

	void init(uint8_t *data, uint8_t len)
	{
		byteCode = data;
		length = len;
		position = 0;
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
		
		*((uint8_t*)&value+0) = *(byteCode + position+3);
		*((uint8_t*)&value+1) = *(byteCode + position+2);
		*((uint8_t*)&value+2) = *(byteCode + position+1);
		*((uint8_t*)&value+3) = *(byteCode + position+0);
		//memcpy((uint8_t*)&value, byteCode + position, 4);
		position+=4;
		return value;
	}
	
	void execute()
	{
		while(position < length)
		{
			uint8_t b = byteCode[position];
			position++;
			
			if(b == 0xc0)
			{
				push(getConst());
				continue;
			}
			
			if(b & 0x40)
			{
				push(variables[b&0x1f]);
				continue;
			}
			
			if(b & 0x80)
			{
				variables[b&0x1f] = pop();
				continue;
			}
			
			switch(b)
			{
				case 1:
					{
						float rv = pop();
						float lv = pop();
						push(lv + rv);
					}
					break;
				
				case 2:
					{
						float rv = pop();
						float lv = pop();
						push(lv - rv);
					}
					break;
				
				case 3:
					{
						float rv = pop();
						float lv = pop();
						push(lv * rv);
					}
					break;
				
				case 4:
					{
						float rv = pop();
						float lv = pop();
						push(lv / rv);
					}
					break;
				
				case 5:
					{
						push(-pop());
					}
					break;
				
				case 6:
					{
						push(sin(pop()));
					}
					break;
				
				case 7:
					{
						push(cos(pop()));
					}
					break;
				
				case 8:
					{
						push(exp(pop()));
					}
					break;
				
				case 10:
					{
						push(sqrt(pop()));
					}
					break;
				
				case 11:
					{
						delay(pop());
					}
					break;
				
				case 12:
					{
						push(millis());
					}
					break;
				
			}
			
		}
		
		position = 0;
	}
	
	
};

#endif
