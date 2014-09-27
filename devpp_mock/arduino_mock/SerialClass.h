#pragma once

#include <memory.h>

class SerialClass
{
public:
	unsigned char buf[1024];
	int len;
	int pos;

	SerialClass(void);
	~SerialClass(void);

	int available(void);
	int read(void);

	void write(int byte)
	{

	}

	void begin(int){};

	void set(const char* data, int length)
	{
		pos = 0;
		len = length;
		memcpy(buf, data, len);
	}

};

extern SerialClass Serial;
