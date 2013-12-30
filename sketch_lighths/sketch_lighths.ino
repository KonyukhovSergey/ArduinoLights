#include <SPI.h>

#include "Screen.h"
#include "Message.h"

#include "ModeSingleColor.h"
#include "ModePointsRGB.h"

#define MODE_SINGLE_COLOR = 0x01

Screen screen;
Message msg;

uint8_t data[256];
int mode;

void setup() 
{
	Serial.begin(115200);
	SPI.begin();
	SPI.setBitOrder(MSBFIRST);
	SPI.setDataMode(SPI_MODE0); 
	SPI.setClockDivider(SPI_CLOCK_DIV16);
	mode = 0;
	delay(1);
	msg.init();
}

void loop() 
{
	if(msg.isMessage() > 0)
	{
		mode = msg.data[0];

		switch (mode)
		{
		case '1':
			((ModeSingleColor*)data)->init(msg.data + 1);
			break;

		case '2':
			((ModePointsRGB*)data)->init();
		}
	}

	switch (mode)
	{
	case '1':
		((ModeSingleColor*)data)->draw(screen);
		break;

	case '2':
		((ModePointsRGB*)data)->draw(screen);
	}

	screen.sendToModules();
	delay(1);
}

