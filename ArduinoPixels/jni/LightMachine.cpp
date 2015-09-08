#include <jni.h>
#include "EEPROM.h"

EEPROMClass EEPROM;

#include "../../sketch_lighths/LightMachine.h"

uint8_t data[MAX_VARIABLES_COUNT * 4 + STACK_SIZE * 4 + 256 + (SCREEN_PIXELS * 4)];

#define DATA_OFFSET_VARIABLES 0
#define DATA_OFFSET_STACK (MAX_VARIABLES_COUNT * 4)
#define DATA_OFFSET_GAMMA (MAX_VARIABLES_COUNT * 4 + STACK_SIZE * 4)
#define DATA_OFFSET_PIXELS (MAX_VARIABLES_COUNT * 4 + STACK_SIZE * 4 + 256)

LightMachine lightMachine;
Screen screen;

extern "C" {

JNIEXPORT jint JNICALL Java_js_jni_code_NativeCalls_init(JNIEnv *env, jclass, jbyteArray byteCode) {
	env->GetByteArrayRegion(byteCode, 0, EEPROM_SIZE, (signed char*) EEPROM.buf);
	screen.init(data + DATA_OFFSET_PIXELS, data + DATA_OFFSET_GAMMA);
	lightMachine.init(data + DATA_OFFSET_VARIABLES, data + DATA_OFFSET_STACK);
	return 0;
}

JNIEXPORT jint JNICALL Java_js_jni_code_NativeCalls_tick(JNIEnv *env, jclass) {
	jint rvalue = lightMachine.tick();
	return rvalue;
}

JNIEXPORT jint JNICALL Java_js_jni_code_NativeCalls_draw(JNIEnv *env, jclass, jintArray buf) {
	unsigned char buff[SCREEN_PIXELS * 4];
	for (int i = 0; i < SCREEN_PIXELS; i++) {
		buff[i * 4 + 3] = 255;
		buff[i * 4 + 2] = SPI.data[i * 3 + 0];
		buff[i * 4 + 1] = SPI.data[i * 3 + 1];
		buff[i * 4 + 0] = SPI.data[i * 3 + 2];
	}
	env->SetIntArrayRegion(buf, 0, SCREEN_PIXELS, (signed int*) buff);
	return 0;
}

}
