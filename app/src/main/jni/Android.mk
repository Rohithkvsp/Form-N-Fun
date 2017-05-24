LOCAL_PATH := $(call my-dir)
export OPENCV_PACKAGE_DIR =C:/opencv/OpenCV-3.0-android-sdk
include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=on
include $(OPENCV_PACKAGE_DIR)/sdk/native/jni/OpenCV.mk

LOCAL_MODULE:= process
LOCAL_SRC_FILES:= processframes.cpp
LOCAL_SRC_FILES += findballsandmazes.cpp
LOCAL_LDLIBS +=  -llog -ldl
include $(BUILD_SHARED_LIBRARY)