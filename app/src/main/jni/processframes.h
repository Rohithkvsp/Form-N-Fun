#include <jni.h>

#ifndef MYAPPLICATION_PROCESSFRAMES_H
#define MYAPPLICATION_PROCESSFRAMES_H

extern "C"
{
    JNIEXPORT jlong JNICALL Java_com_formfun_FindMazesAndBalls_newSelf(JNIEnv* env, jobject, jint screen_width,jint screen_height, jint mat_width, jint mat_height);
    JNIEXPORT void JNICALL Java_com_formfun_FindMazesAndBalls_deleteSelf(JNIEnv* env, jobject, jlong selfAddr);
    JNIEXPORT void JNICALL Java_com_formfun_FindMazesAndBalls_run(JNIEnv* env, jobject, jlong selfAddr, jlong addrRgba);
    JNIEXPORT jboolean JNICALL Java_com_formfun_FindMazesAndBalls_foundbothlineandball(JNIEnv* env, jobject, jlong selfAddr);
    JNIEXPORT jobject JNICALL  Java_com_formfun_graphics_GraphicThread_getmazesarraylist(JNIEnv *env, jobject obj,jlong selfAddr);
    JNIEXPORT jobject JNICALL  Java_com_formfun_graphics_GraphicThread_getballsarraylist(JNIEnv *env, jobject obj,jlong selfAddr);

}



#endif //MYAPPLICATION_PROCESSFRAMES_H
