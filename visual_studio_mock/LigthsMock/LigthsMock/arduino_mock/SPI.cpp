#include "SPI.h"


SPIClass::SPIClass(int size)
{
	size = size * 3;
	data = new uint8_t[size];
	len = size;
	pos = 0;
	hdc = GetDC(0);

}


SPIClass::~SPIClass(void)
{
	if(data != 0)
	{
		delete[] data;
		data = 0;
		ReleaseDC(0,hdc);
	}
}

void SPIClass::transfer(int byte)
{
	data[pos] = byte;

	pos++;

	if(pos == len)
	{
		pos = 0;
	}
}

void SPIClass::draw()
{

	for(int i = 0; i < len/3; i++)
	{
		COLORREF cr = RGB(data[i*3],data[i*3+1],data[i*3+2]);

		SetPixel(hdc, i *4+10,10,cr);
		SetPixel(hdc, i *4+10,11,cr);
		SetPixel(hdc, i *4+10,12,cr);
		SetPixel(hdc, i *4+10,13,cr);
 
		SetPixel(hdc, i*4+11,10,cr);
		SetPixel(hdc, i*4+11,11,cr);
		SetPixel(hdc, i*4+11,12,cr);
		SetPixel(hdc, i*4+11,13,cr);

		SetPixel(hdc,i*4+12,10,cr);
		SetPixel(hdc,i*4+12,11,cr);
		SetPixel(hdc,i*4+12,12,cr);
		SetPixel(hdc,i*4+12,13,cr);

		SetPixel(hdc,i*4+13,10,cr);
		SetPixel(hdc,i*4+13,11,cr);
		SetPixel(hdc,i*4+13,12,cr);
		SetPixel(hdc,i*4+13,13,cr);

		//RECT rect = {i*16, 16, i * 16 + 15, 16 + 15};
		//HBRUSH brush = CreateSolidBrush();
		//FillRect(hdc, &rect, brush);
		//DeleteObject(brush);
	}
}

SPIClass SPI(50);