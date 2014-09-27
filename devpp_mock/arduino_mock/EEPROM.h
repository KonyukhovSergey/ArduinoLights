#pragma once
#define EEPROM_SIZE 1024
#define EEPROM_FILE "eeprom.bts"

#include <stdio.h>

class EEPROMClass
{
	private:
	 	unsigned char buf[EEPROM_SIZE];

	public:

	    EEPROMClass()
	    {
	        FILE *fl = fopen(EEPROM_FILE, "rb");

	        if(fl != NULL)
            {
                fread(buf, 1, EEPROM_SIZE, fl);
                fclose(fl);
            }
	    }

	    ~EEPROMClass()
	    {
	        FILE *fl = fopen(EEPROM_FILE, "wb");
	        fwrite(buf, 1, EEPROM_SIZE, fl);
	        fclose(fl);
	    }

		unsigned char read(int pos)
		{
			return buf[pos];
		}

		void write(int pos, unsigned char value)
		{
			buf[pos] = value;
		}
};

extern EEPROMClass EEPROM;
