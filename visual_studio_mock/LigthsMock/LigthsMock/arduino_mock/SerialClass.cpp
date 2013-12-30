#include "SerialClass.h"
#include <stdio.h>

#define FILE_SERIAL_PORT_DATA "serial_port_data.txt"

SerialClass::SerialClass(void)
{
	len = 0;
	pos = 0;
}


SerialClass::~SerialClass(void)
{
}


int SerialClass::available(void)
{
	if(pos == len)
	{
		FILE *fl;

		fopen_s(&fl, FILE_SERIAL_PORT_DATA, "rb");

		if(fl)
		{
			fseek(fl, 0, SEEK_END);
			len = ftell(fl);
			fseek(fl, 0, SEEK_SET);
			fread(buf, len, 1, fl);
			fclose(fl);

			remove(FILE_SERIAL_PORT_DATA);
		}
	}

	return len - pos;
}

int SerialClass::read(void)
{
	if(pos < len)
	{
		pos++;
		return buf[pos - 1];
	}

	return -1;
}

SerialClass Serial;