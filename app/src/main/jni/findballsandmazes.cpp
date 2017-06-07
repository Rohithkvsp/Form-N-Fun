//
// Created by ROHIT on 22-05-2017.
//

#include "findballsandmazes.h"
#include <vector>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <android/log.h>


findballsandmazes::findballsandmazes(int screen_width,int screen_height, int mat_width, int mat_height)
{
       scale=min( (float)screen_width/mat_width, (float)screen_height/mat_height );  // find scale (defined in opencv JavaCameraView.java class at line 167)
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "screen_width %d", screen_width);
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "screen_height  %d", screen_height);
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "mat_height %d", mat_height);
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "mat_width %d", mat_width);
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "scale %f", scale);

       xoffset=(float)(screen_width-scale*mat_width)/2.0; //shift the points X coordiante by xoffset (defined in opencv CameraBridgeViewBase.java at line 420)
       yoffset=(float)(screen_height-scale*mat_height)/2.0; //shift the points Y coordiante by yoffset (defined in opencv CameraBridgeViewBase.java at line 421)
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "xoffset %f", xoffset);
       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "yoffset %f", yoffset);
}

/*
   processing frames
*/

void findballsandmazes::run(Mat &mRgb)
{

         cvtColor(mRgb,grayscale, CV_BGR2GRAY); //convert frame to grayscale
         GaussianBlur(grayscale, grayscale, Size(7,7), 2, 2); // perform gaussian blur to remove noise
         adaptiveThreshold(grayscale, grayscale,255, ADAPTIVE_THRESH_GAUSSIAN_C,THRESH_BINARY_INV,7,7 ); // apply adaptive thresolding to blured frame
         dilate(grayscale,grayscale,getStructuringElement(MORPH_RECT,Size(5,5 ),Point(-1,-1 ))); // dilate the frame to fill gaps
         erode(grayscale,grayscale, getStructuringElement(MORPH_RECT,Size(3,3 ),Point(-1,-1 ))); // erode the dilated frame to reduce the thickness of mazes and balls
         findContours( grayscale, contours, hierarchy,CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE ); // find contours

         rigidsurface.clear(); // clear mazes buffer
         ballcenter.clear();  // clear balls buffer

         vector< vector<Point> > approx(contours.size());
         for( int i = 0; i< contours.size(); i++ )  // iterate over each contours
         {

               approxPolyDP(contours[i], approx[i], arcLength(Mat(contours[i]), true)*0.1, true);
               mc.clear();
               approx_area = contourArea(approx[i]);
               /*
                  this is to detect balls
                  check the boundary is convex
                  check the boundary circularity > 0.4 (if circularity is 1 then perfect circle)
                  check for boundary area < 350
                  hierarchy[i][4]!=-1 and hierarchy[i][3]!=-1 looking for boundary inside boundary
               */
               if(isContourConvex(approx[i]) && isCircle(approx[i],approx_area) && approx_area > 40.0 && approx_area < 400.0 && hierarchy[i][4]== -1 && hierarchy[i][3] == -1)
               {


                  drawContours( mRgb, contours, i, Scalar(0,255,255), 2, 8, hierarchy, 0, Point() ); // draw greenish color to boundary
                  minEnclosingCircle( contours[i],  center, radius); // enclose the contour to circle
                  /// bondaries need to be scaled and translated because opencv will streched the frame to display and streched frame may not be displayed from top left
                  mc.push_back(Point2f(center.x*scale+xoffset,center.y*scale+yoffset)); // add scaled & translated boundry center
                  mc.push_back(Point2f( radius*scale,0 )); //add ball radius //add  scaled & translated boundry radius
                  ballcenter.push_back(mc); //// add scaled & translated balls center and raduis to buffer
               }

               /*
                    this is to detect mazes
                    if contours is not perfect convex then it is maze (only this conditions is sufficient)
               */
               else if(!isContourConvex(approx[i])) //&&CircularityStandard(approx[i])<0.4&&arcLength(Mat(approx[i]),true)>450)
               {
                     contourshifted.clear(); // clear the scaled & translated boundry points
                     /// bondaries need to be scaled and translated because opencv will streched the frame to display and streched frame may not be displayed from top left
                     for(int j=0; j < contours[i].size(); j++)
                        contourshifted.push_back(Point2f(contours[i][j].x*scale+xoffset,contours[i][j].y*scale+yoffset));

                     rigidsurface.push_back(contourshifted); // add scaled & translated boundry points to maze buffer
                     drawContours( mRgb, contours, i, Scalar(255,0,0), 2, 8, hierarchy, 0, Point() ); // draw red color to boundary

                }

          }
}

/*
   returns true if contour is circle
*/

bool findballsandmazes::isCircle(const vector<cv::Point> &contour, double contourArea)
{
  if(contourArea ==0) return false;
  Point2f _center;
  float _radius = 0.0;
  minEnclosingCircle( contour,  _center, _radius); // enclose the contour to circle
  float circleArea = CV_PI * _radius * _radius;
  return ((circleArea/contourArea)>0.75) && (CircularityStandard(contour,contourArea)>0.4);
}

/*
  returns the cicularity of the contour
*/

double findballsandmazes::CircularityStandard(const vector<cv::Point> &contour, double contourArea)
{
    double perimeter = cv::arcLength(contour, true);
    if (perimeter == 0) return 0.0;
    return (4 * CV_PI*contourArea) / (perimeter*perimeter);
}

/*
  returns the distance between two points
*/

float findballsandmazes::Distance(Point2f& p, Point2f& q) {
    Point diff = p - q;
    return cv::sqrt(diff.x*diff.x + diff.y*diff.y);
}

/*
  returns reference to vector of vector point corresponding to mazes (maze buffer)
*/

 vector< vector <Point2f> > & findballsandmazes::getRigidSurfaces()
 {
    return rigidsurface;
 }

 /*
   returns reference to vector of vector point corresponding to balls (ball buffer)
 */
 vector< vector <Point2f> > & findballsandmazes::getBalls()
 {
    return ballcenter;
 }