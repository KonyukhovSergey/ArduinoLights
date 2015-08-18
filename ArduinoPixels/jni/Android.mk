LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := LightMachine
LOCAL_SRC_FILES := LightMachine.cpp Arduino.cpp SPI.cpp

include $(BUILD_SHARED_LIBRARY)
