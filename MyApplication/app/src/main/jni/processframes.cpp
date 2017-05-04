//
// Created by ROHIT on 04-10-2016.
//

#include "processframes.h"
#include <jni.h>
#include<vector>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <android/log.h>
#define APPNAME "MyApplication"

using namespace std;
using namespace cv;


Size s;
Mat grayscale(s, CV_8UC1);

vector<Point2f> quad_pts ;

vector<vector <Point> > contours; // Vector for storing contour
vector<Vec4i> hierarchy;
int matwidth;
int matheight;
int rigidcount=0;
int largest_area=0;
vector<vector <Point2f> > rigidsurface;
vector<vector <Point2f> > ballcenter;
vector<vector <Point2f> > hullpoints;
vector <Point2f> hp;
Moments mu;
vector<Point2f> mc;
Point2f center;
vector<Point2f> contourshifted;
vector<vector<Point> >hull;




float radius=0.0;
float scale=0.0;
float xoffset=0.0;
float yoffset=0.0;

int _index=-1;

extern "C" {



double CircularityStandard(const vector<cv::Point> &contour)
{
    double area = cv::contourArea(contour);
    if (area == 0) return 0.0;
    double perimeter = cv::arcLength(contour, true);
    if (perimeter == 0) return 0.0;
    return (4 * CV_PI*area) / (perimeter*perimeter);
}

float Distance(Point2f& p, Point2f& q) {
    Point diff = p - q;
    return cv::sqrt(diff.x*diff.x + diff.y*diff.y);
}


JNIEXPORT jint JNICALL Java_com_example_rohit_myapplication_Detect_process(JNIEnv* env, jobject , jlong addrRgba, jlong addrGray,jint screenwidth,jint screenheight)
{

 Mat& mRgb = *(Mat*)addrRgba;
 Mat& mDect = *(Mat*)addrGray;
 s=mRgb.size();
 matwidth=s.width;
 matheight=s.height;
 scale=min( (float)screenwidth/matwidth, (float)screenheight/matheight );

 //__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "scale %f", scale);
 xoffset=(float)(screenwidth-scale*matwidth)/2.0;
 yoffset=(float)(screenheight-scale*matheight)/2.0;
 //__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "xoffset %f", xoffset);
// __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "yoffset %f", yoffset);

 cvtColor(mRgb,grayscale, CV_BGR2GRAY);
 GaussianBlur(grayscale, grayscale, Size(7,7), 2, 2);
 adaptiveThreshold(grayscale, grayscale,255, ADAPTIVE_THRESH_GAUSSIAN_C,THRESH_BINARY_INV,7,7 );
 dilate(grayscale,grayscale,getStructuringElement(MORPH_RECT,Size(5,5 ),Point(-1,-1 )));
 erode(grayscale,grayscale, getStructuringElement(MORPH_RECT,Size(3,3 ),Point(-1,-1 )));
  findContours( grayscale, contours, hierarchy,CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE );
  mDect=mRgb;

  rigidsurface.clear();
  ballcenter.clear();

  vector< vector<Point> > approx(contours.size());
  _index=-1;
  largest_area=0;
  for( int i = 0; i< contours.size(); i++ )
  {
      //double area0 = contourArea(contours[i]);
       //vector<Point> approx;
       //drawContours( mDect, contours, i, Scalar(255,0,0), -1, 8, hierarchy, 0, Point());


       approxPolyDP(contours[i], approx[i], arcLength(Mat(contours[i]), true)*0.1, true);

       /* __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "contours %d", i);
       //__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "contours  size %d", area0);
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "contourArea(contour)  %lf", CircularityStandard(approx[i]));
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "CircularityStandard(approx) %lf", CircularityStandard(approx[i]));
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "isContourConvex(approx) %d",isContourConvex(approx[i]));
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "contourArea(approx) %lf",contourArea(approx[i]));
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "arcLength(Mat(approx)) %lf",arcLength(Mat(approx[i]),true));
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "____________");
       */



       mc.clear();

       if(isContourConvex(approx[i])&&CircularityStandard(approx[i])>0.4&&contourArea(approx[i])>40.0&&contourArea(approx[i])<350.0&&hierarchy[i][4]== -1&&hierarchy[i][3]==-1)
       {
          __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "hierarchy[i][4] = %d", hierarchy[i][4]);
          __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "hierarchy[i][3] = %d", hierarchy[i][3]);

          drawContours( mDect, contours, i, Scalar(0,255,255), 2, 8, hierarchy, 0, Point() );
          minEnclosingCircle( contours[i],  center, radius);
         // mu = moments( contours[i], false );
          //mc.push_back(Point2f( mu.m10/mu.m00 , mu.m01/mu.m00 ));
          mc.push_back(Point2f(center.x*scale+xoffset,center.y*scale+yoffset));
          mc.push_back(Point2f( radius*scale,0 ));
          ballcenter.push_back(mc);
       }
       else if(!isContourConvex(approx[i]))//&&CircularityStandard(approx[i])<0.4&&arcLength(Mat(approx[i]),true)>450)
       {
        //convexHull( Mat(contours[i]), hull[i], false );
        double area0 = contourArea(contours[i]);
        if(area0>largest_area)
        {
            _index=i;
            largest_area=area0;
        }

         contourshifted.clear();
         for(int j=0;j<contours[i].size();j++)
            contourshifted.push_back(Point2f(contours[i][j].x*scale+xoffset,contours[i][j].y*scale+yoffset));

         rigidsurface.push_back(contourshifted);
         drawContours( mDect, contours, i, Scalar(255,0,0), 2, 8, hierarchy, 0, Point() );
         //drawContours( mDect, hull, i, Scalar(0,0,255), 3, 8, hierarchy, 0, Point());

        }

   }

  // hull.clear();
   //hullpoints.clear();
   //hp.clear();


  /* if(_index!=-1)
   {
      vector<Point> hullpoint;
      //vector<int> hullpointI;
      convexHull( Mat(contours[_index]), hullpoint, false );
      //convexHull( Mat(contours[_index]), hullpointI, false );
      hull.push_back(hullpoint);
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "conter[_index] size %d", contours[_index].size());
      __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "hull size %d", hullpoint.size());
      for(int i=0;i<hullpoint.size();i++)
        hp.push_back(Point2f(hullpoint[i].x*scale+xoffset,hullpoint[i].y*scale+yoffset));
      hullpoints.push_back(hp);
      drawContours( mDect, hull,0, Scalar(0,0,255), 2, 8, hierarchy, 0, Point());
       */


      /*vector<Vec4i> defects;
      __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "start %d", hullpointI.size());
      convexityDefects( Mat(contours[_index]),hullpointI, defects);
      __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "deects size %d",defects.size() );

       for(int i=0;i<defects.size();i++) {
              int startidx=defects[i][0];
              Point ptStart( contours[_index][startidx] ); // point of the contour where the defect begins
              int endidx=defects[i][1];
              int faridx=defects[i][2];
              __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "_index %d", i);
              __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "start %d", startidx);
                __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "end %d", endidx);
              Point ptEnd(contours[_index][endidx] ); // point of the contour where the defect ends
              Point ptFar(contours[_index][faridx] );
              line( mDect, ptStart, ptFar, CV_RGB(0,255,0), 2 );
              line( mDect, ptFar, ptEnd, CV_RGB(0,255,0), 2 );
              circle( mDect, ptFar,   4, Scalar(100,0,255), 2 );
              circle( mDect, ptStart,   4, Scalar(100,0,255), 2 );
              circle( mDect, ptEnd,   4, Scalar(100,0,255), 2 );

      }
     */


   //}


      /**vector<Point>  approxCurve;

      approxPolyDP(hullpoint, approxCurve, arcLength(Mat(hullpoint), true)*0.1, true);
      __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "approx size %d", approxCurve.size());

      if(approxCurve.size()==4)
      {
        vector<Point2f> refined_points;
        for(int i=0;i<4;i++)
           refined_points.push_back(Point2f(approxCurve[i].x,approxCurve[i].y));
        Point v1 = refined_points[1] - refined_points[0];
        Point v2 = refined_points[2] - refined_points[0];

        double o = (v1.x * v2.y) - (v1.y * v2.x);
        if (o < 0.0)
            std::swap(refined_points[1], refined_points[3]);

        if(Distance(refined_points[0],refined_points[3])>Distance(refined_points[0],refined_points[1]))
         {
               rotate(refined_points.begin(),refined_points.begin()+3,refined_points.end());
         }
         Mat quad = cv::Mat::zeros((int)Distance(refined_points[0],refined_points[3]),(int) Distance(refined_points[0],refined_points[1]), CV_8UC3);
         //__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "dist 0 3 %lf",Distance(refined_points[0],refined_points[3]));
         //__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "dist 0 1 %lf",Distance(refined_points[0],refined_points[1]));


        quad_pts.push_back(Point2f(0, 0));
        quad_pts.push_back(Point2f(quad.cols-1, 0));
        quad_pts.push_back(Point2f(quad.cols-1, quad.rows-1));
        quad_pts.push_back(Point2f(0, quad.rows-1));
        Mat transmtx = getPerspectiveTransform(refined_points, quad_pts);
        warpPerspective(mDect, quad, transmtx, quad.size());
        quad.copyTo(mDect(Rect(0,0,quad.cols,quad.rows)));
        quad_pts.clear();

      }
    */


  }



  JNIEXPORT jobjectArray JNICALL Java_com_example_rohit_myapplication_Detect_getCircle(JNIEnv* env,jobject)
  {


     jclass java_line_cls = env->FindClass("com/example/rohit/myapplication/BoundaryPoints");
     jmethodID java_line_add = env->GetMethodID(java_line_cls, "addPoint", "(II)V");
     jmethodID java_line_init = env->GetMethodID(java_line_cls, "<init>", "()V");

     jobjectArray boundarypoints = (jobjectArray) env->NewObjectArray(contours.size(), java_line_cls, 0);

     for(int i = 0; i < contours.size(); ++i)
     {
         jobject curl =  env->NewObject(java_line_cls, java_line_init);
         for(int j = 0; j < contours[i].size(); ++j)
             env->CallVoidMethod(curl, java_line_add,
                                     contours[i][j].x,
                                     contours[i][j].y);
         env->SetObjectArrayElement(boundarypoints, i, curl);
     }
     return boundarypoints;
  }



JNIEXPORT jobjectArray JNICALL Java_com_example_rohit_myapplication_GraphicThread_getrigidsurface(JNIEnv* env,jobject)
  {


     jclass java_line_cls = env->FindClass("com/example/rohit/myapplication/BoundaryPoints");
     jmethodID java_line_add = env->GetMethodID(java_line_cls, "addPoint", "(FF)V");
     jmethodID java_line_init = env->GetMethodID(java_line_cls, "<init>", "()V");

     jobjectArray boundarypoints = (jobjectArray) env->NewObjectArray(rigidsurface.size(), java_line_cls, 0);

     for(int i = 0; i < rigidsurface.size(); ++i)
     {
         jobject curl =  env->NewObject(java_line_cls, java_line_init);
         for(int j = 0; j < rigidsurface[i].size(); ++j)
             env->CallVoidMethod(curl, java_line_add,
                                     rigidsurface[i][j].x,
                                     rigidsurface[i][j].y);
         env->SetObjectArrayElement(boundarypoints, i, curl);
     }
     return boundarypoints;
  }


JNIEXPORT jobjectArray JNICALL Java_com_example_rohit_myapplication_GraphicThread_getballPoints(JNIEnv* env,jobject)
  {


     jclass java_line_cls = env->FindClass("com/example/rohit/myapplication/BoundaryPoints");
     jmethodID java_line_add = env->GetMethodID(java_line_cls, "addPoint", "(FF)V");
     jmethodID java_line_init = env->GetMethodID(java_line_cls,"<init>", "()V");

     jobjectArray boundarypoints = (jobjectArray) env->NewObjectArray(ballcenter.size(), java_line_cls, 0);

     for(int i = 0; i < ballcenter.size(); ++i)
     {
         jobject curl =  env->NewObject(java_line_cls, java_line_init);
        for(int j = 0; j < ballcenter[i].size(); ++j)
             env->CallVoidMethod(curl, java_line_add,
                                     ballcenter[i][j].x,
                                     ballcenter[i][j].y);
         env->SetObjectArrayElement(boundarypoints, i, curl);
     }
     return boundarypoints;
  }

/*
  JNIEXPORT jobjectArray JNICALL Java_com_example_rohit_myapplication_GraphicThread_gethullPoints(JNIEnv* env,jobject)
    {


       jclass java_line_cls = env->FindClass("com/example/rohit/myapplication/BoundaryPoints");
       jmethodID java_line_add = env->GetMethodID(java_line_cls, "addPoint", "(FF)V");
       jmethodID java_line_init = env->GetMethodID(java_line_cls,"<init>", "()V");

       jobjectArray boundarypoints = (jobjectArray) env->NewObjectArray(hullpoints.size(), java_line_cls, 0);

       for(int i = 0; i < hullpoints.size(); ++i)
       {
           jobject curl =  env->NewObject(java_line_cls, java_line_init);
          for(int j = 0; j < hullpoints[i].size(); ++j)
               env->CallVoidMethod(curl, java_line_add,
                                       hullpoints[i][j].x,
                                       hullpoints[i][j].y);
           env->SetObjectArrayElement(boundarypoints, i, curl);
       }
       return boundarypoints;
    }

*/


}
