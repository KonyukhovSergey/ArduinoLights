#include <Arduino.h>

#include <stdlib.h>
#include <time.h>

void delay(int value) {

//	value += millis();
//	while (millis() < value)
//		;
	//   Sleep(value);
}

int analogRead(int pin) {
	return 0;
}

void randomSeed(int value) {
}

int random(int maxValue) {
	return rand() % maxValue;
}

int millis() {
	return clock() / 1000;
}
