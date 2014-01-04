#include <windows.h>

#include <SPI.h>
#include <SerialClass.h>


#include "Screen.h"
#include "Message.h"

#include "ModeSingleColor.h"
#include "ModePointsRGB.h"
#include "ModeAurora.h"

Screen screen;
Message msg;

drawFunction draw;

void setup() 
{
	Serial.begin(115200);
	SPI.begin();
	SPI.setBitOrder(MSBFIRST);
	SPI.setDataMode(SPI_MODE0); 
	SPI.setClockDivider(SPI_CLOCK_DIV16);
	draw = 0;
	delay(1);
	msg.init();
}

void loop() 
{
	if(msg.isMessage() > 0)
	{
		switch (msg.data[0])
		{
		case 0x01:
			draw = ((ModeSingleColor*)data)->init(msg.data + 1);
			break;

		case 0x02:
			draw = ((ModePointsRGB*)data)->init();
			break;

		case 0x03:
			draw = ((ModeAurora*)data)->init();
			break;
		}
	}

	if(draw != 0)
	{
		draw(screen);
	}

	screen.sendToModules();
	
	delay(1);
}

int main(int argc, char** argv)
 {
 	
	setup();
	
	int i = 0;

	while(true)
	{
		
		loop();
		SPI.draw();
		
		
		switch(i/10)
		{
			case 1:
			 	Serial.set("\x04\x01\xff\x00\xff", 5);
				break;
				
			case 2:
			 	Serial.set("\x01\x02", 2);
				break;
				
			case 3:
			 	Serial.set("\x01\x03", 2);
				break;
				
//			case 9:
//				i = 0;
//				break;
		}
		
		i++;
		
	}
	
	return 0;
}
