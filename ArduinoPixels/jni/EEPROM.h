#ifndef EEPROM_H
#define EEPROM_H
#define EEPROM_SIZE 1024

#include <stdio.h>
#include <stdlib.h>

class EEPROMClass {

public:
	unsigned char buf[EEPROM_SIZE];

	EEPROMClass() {
		memset(buf, 0, EEPROM_SIZE);
	}

	~EEPROMClass() {
	}

	unsigned char read(int pos) {
		return buf[pos];
	}

	void write(int pos, unsigned char value) {
		buf[pos] = value;
	}
};

extern EEPROMClass EEPROM;
#endif
