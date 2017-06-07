#include "processframes.h"
#include "findballsandmazes.h"
#include <jni.h>
#include <android/log.h>

using namespace std;

extern "C" {

    // create instance and return the address
    JNIEXPORT jlong JNICALL Java_com_formfun_FindMazesAndBalls_newSelf(JNIEnv* env, jobject, jint screen_width,jint screen_height, jint mat_width, jint mat_height)
    {
        findballsandmazes *self = new findballsandmazes(screen_width,screen_height,mat_width,mat_height);
         __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Created c++ object");
        return (jlong)self;  //cast address type  tojlong
    }

    // delete the instance
    JNIEXPORT void JNICALL Java_com_formfun_FindMazesAndBalls_deleteSelf(JNIEnv* env, jobject, jlong selfAddr)
    {
       if (selfAddr != 0)
       {
           findballsandmazes *self = (findballsandmazes *)selfAddr;
             __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "deleted c++ object");
           delete self;
       }
    }

    JNIEXPORT void JNICALL Java_com_formfun_FindMazesAndBalls_run(JNIEnv* env, jobject, jlong selfAddr, jlong addrRgba)
    {

        if (selfAddr != 0)
        {
             findballsandmazes *self = (findballsandmazes *)selfAddr; //type cast to findballsandmazes *
             Mat& mRgb = *(Mat*)addrRgba; // type cast to Mat* then defer
             self->run(mRgb);
        }
    }

    JNIEXPORT jboolean JNICALL Java_com_formfun_FindMazesAndBalls_foundbothlineandball(JNIEnv* env, jobject, jlong selfAddr)
    {

        if (selfAddr != 0)
        {
             findballsandmazes *self = (findballsandmazes *)selfAddr; //type cast to findballsandmazes *
             vector< vector <Point2f> >& mazepoints = self->getRigidSurfaces(); //get mazepoints
             vector< vector <Point2f> >& ballpoints = self->getBalls(); //get ball points
             if(mazepoints.size() != 0 && ballpoints.size()!= 0)
                return true;
              else
                return false;
        }
    }

    /**
    *  return arraylist of BoundaryPoints objects corresponding to maze
    */
    JNIEXPORT jobject JNICALL  Java_com_formfun_graphics_GraphicThread_getmazesarraylist(JNIEnv *env, jobject obj,jlong selfAddr)
    {
        if (selfAddr == 0)
        {
            return NULL;
        }


        findballsandmazes *self = (findballsandmazes *)selfAddr;
        vector< vector <Point2f> >& points = self->getRigidSurfaces();
        if(points.size()==0)
            return NULL;
        jclass java_util_ArrayList      =  env->FindClass("java/util/ArrayList");
        jmethodID java_util_ArrayList_init     = env->GetMethodID(java_util_ArrayList, "<init>", "(I)V");
        jmethodID java_util_ArrayList_size = env->GetMethodID (java_util_ArrayList, "size", "()I");
        jmethodID java_util_ArrayList_get  = env->GetMethodID(java_util_ArrayList, "get", "(I)Ljava/lang/Object;");
        jmethodID java_util_ArrayList_add  = env->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");

        jclass java_curve_cls = env->FindClass("com/formfun/graphics/BoundaryPoints");
        jmethodID java_curve_add = env->GetMethodID(java_curve_cls, "addPoint", "(FF)V");
        jmethodID java_curve_init = env->GetMethodID(java_curve_cls,"<init>", "()V");


        jobject curvepoints = env->NewObject(java_util_ArrayList, java_util_ArrayList_init, points.size());
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "arraylist maze size = %d",points.size());
        for(int i = 0; i < points.size(); ++i)
        {
           //__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "points[%d] size %d",i,points[i].size());
           jobject curl =  env->NewObject(java_curve_cls, java_curve_init);
           for(int j = 0; j < points[i].size(); ++j)
                    env->CallVoidMethod(curl, java_curve_add, points[i][j].x,points[i][j].y);
           env->CallBooleanMethod(curvepoints, java_util_ArrayList_add, curl);
           env->DeleteLocalRef(curl);

        }
        return curvepoints;

    }

    /**
    *  return arraylist of BoundaryPoints objects corresponding to ball
    */
    JNIEXPORT jobject JNICALL  Java_com_formfun_graphics_GraphicThread_getballsarraylist(JNIEnv *env, jobject obj,jlong selfAddr)
    {
        if (selfAddr == 0)
        {
            return NULL;
        }


        findballsandmazes *self = (findballsandmazes *)selfAddr;
        vector< vector <Point2f> >& points = self->getBalls();
        if(points.size()==0)
            return NULL;
        jclass java_util_ArrayList      =  env->FindClass("java/util/ArrayList");
        jmethodID java_util_ArrayList_init     = env->GetMethodID(java_util_ArrayList, "<init>", "(I)V");
        jmethodID java_util_ArrayList_size = env->GetMethodID (java_util_ArrayList, "size", "()I");
        jmethodID java_util_ArrayList_get  = env->GetMethodID(java_util_ArrayList, "get", "(I)Ljava/lang/Object;");
        jmethodID java_util_ArrayList_add  = env->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");

        jclass java_ball_cls = env->FindClass("com/formfun/graphics/BoundaryPoints");
        jmethodID java_ball_add = env->GetMethodID(java_ball_cls, "addPoint", "(FF)V");
        jmethodID java_ball_init = env->GetMethodID(java_ball_cls,"<init>", "()V");


        jobject ballpoints = env->NewObject(java_util_ArrayList, java_util_ArrayList_init, points.size());
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "arraylist ball size = %d",points.size());
        for(int i = 0; i < points.size(); ++i)
        {
           //__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "points[%d] size %d",i,points[i].size());
           jobject curl =  env->NewObject(java_ball_cls, java_ball_init);
           for(int j = 0; j < points[i].size(); ++j)
                    env->CallVoidMethod(curl, java_ball_add, points[i][j].x,points[i][j].y);
           env->CallBooleanMethod(ballpoints, java_util_ArrayList_add, curl);
           env->DeleteLocalRef(curl);

        }
        return ballpoints;

    }





}

