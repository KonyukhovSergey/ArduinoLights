#pragma once
#include <Arduino.h>

#define MSBFIRST 0
#define SPI_MODE0 1
#define SPI_CLOCK_DIV16 2

class SPIClass
{
public:
	uint8_t *data;
	uint32_t len;
	uint32_t pos;

	HDC hdc;

	SPIClass(int size);
	~SPIClass(void);

	void transfer(int byte);
	void draw();
	void begin(){};
	void setBitOrder(int bitOrder){	};
	void setDataMode(int dataNode){	};
	void setClockDivider(int clockDivider){	};
	
};

extern SPIClass SPI;

