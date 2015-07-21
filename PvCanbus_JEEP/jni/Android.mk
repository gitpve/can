LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PRELINK_MODULE := false
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
LOCAL_SHARED_LIBRARIES := libui \
			  libutils \
			  libcutils \

LOCAL_MODULE    := libCanbusJEEP
LOCAL_SRC_FILES := cJSON.c canbus.c CanbusInterface.cpp
LOCAL_MODULE_TAGS := optional
include $(BUILD_SHARED_LIBRARY)
