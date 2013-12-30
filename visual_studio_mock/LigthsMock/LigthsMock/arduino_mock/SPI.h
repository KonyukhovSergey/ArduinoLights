#pragma once
#include <Arduino.h>

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
};

extern SPIClass SPI;

