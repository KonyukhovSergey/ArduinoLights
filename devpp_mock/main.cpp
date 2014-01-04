#include <windows.h>

#include <SPI.h>
#include <SerialClass.h>


#include "Screen.h"
#include "Message.h"

#include "ModeSingleColor.h"
#include "ModePointsRGB.h"
#include "ModeAurora.h"
#include "ModeProg.h"

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
			
		case 0x04:
			draw = ((ModeProg*)data)->init(msg.data + 1, msg.position - 1);
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
				
			case 4:
				Serial.set("\x54\x04\xc0\x43\x00\x00\x00\x53\xc0\x40\x00\x00\x00\x03\x06\x03\xc0\x43\x00\x00\x00\x01\x91\xc0\x43\x00\x00\x00\x57\xc0\x3e\x99\x99\x9a\x03\xc0\x40\x00\x00\x00\x01\x53\xc0\x40\x40\x00\x00\x03\x01\x06\x03\xc0\x43\x00\x00\x00\x01\x86\xc0\x43\x00\x00\x00\x57\xc0\x3d\xcc\xcc\xcd\x03\xc0\x40\x80\x00\x00\x01\x06\x03\xc0\x43\x00\x00\x00\x01\x81", 85);
				i=50;
				break;
					
				
//			case 9:
//				i = 0;
//				break;
		}
		
		i++;
		
	}
	
	return 0;
}
