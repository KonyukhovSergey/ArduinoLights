#pragma once

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
};

extern SerialClass Serial;