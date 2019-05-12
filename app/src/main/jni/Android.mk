LOCAL_PATH := $(call my-dir)
export OPENCV_PACKAGE_DIR =/Users/rohithkvsp/Documents/Android/openCV-android-sdk-4.1.0
include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=on
include $(OPENCV_PACKAGE_DIR)/sdk/native/jni/OpenCV.mk

LOCAL_MODULE:= process
LOCAL_SRC_FILES:= processframes.cpp
LOCAL_SRC_FILES += findballsandmazes.cpp
LOCAL_LDLIBS +=  -llog -ldl

LOCAL_CPPFLAGS := -DCV4

include $(BUILD_SHARED_LIBRARY)