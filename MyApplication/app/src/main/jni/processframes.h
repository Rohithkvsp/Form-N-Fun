//
// Created by ROHIT on 04-10-2016.
//
#include <jni.h>

#ifndef MYAPPLICATION_PROCESSFRAMES_H
#define MYAPPLICATION_PROCESSFRAMES_H


extern "C"
{
   JNIEXPORT jint JNICALL Java_com_example_rohit_myapplication_Detect_process(JNIEnv*, jobject, jlong addrRgba, jlong addrGray,jint screenwidth,jint screenheight);

}



#endif //MYAPPLICATION_PROCESSFRAMES_H
