#include <jni.h>

#include "..\\..\\sketch_lighths\\LightMachine.h"

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

JNIEXPORT jint JNICALL Java_js_jni_code_NativeCalls_exec(JNIEnv *env, jclass, jbyteArray buf) {
	jint rvalue = lightMachine.execute();
	env->SetByteArrayRegion(buf, 0, SCREEN_PIXELS * 3, (signed char*) screen.pixels);
	return rvalue;
}

}
