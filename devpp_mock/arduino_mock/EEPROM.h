#pragma once

class EEPROMClass
{
	private:
	 	unsigned char buf[1024];
	 	
	public:
		
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
