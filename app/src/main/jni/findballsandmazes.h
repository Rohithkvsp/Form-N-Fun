//
// Created by ROHIT on 22-05-2017.
//

#ifndef MYAPPLICATION2_FINDBALLSANDMAZES_H
#define MYAPPLICATION2_FINDBALLSANDMAZES_H

#define APPNAME "MyApplication_ndk"
#include <vector>
#include <opencv2/core/core.hpp>

using namespace std;
using namespace cv;



class findballsandmazes {

 public:
        findballsandmazes(int scren_width,int screen_height, int mat_width, int mat_height);
        void run(Mat &mRgb); //processing frames is done here
        vector< vector <Point2f> > & getRigidSurfaces(); //returns reference to vector of vector point corresponding to mazes
        vector< vector <Point2f> > & getBalls();  // //returns reference to vector of vector point corresponding to balls

  private:

          Mat grayscale; // Mat to contain grayscale frame
          double approx_area = 0.0;
          float radius = 0.0;
          float scale = 0.0;
          float xoffset = 0.0;
          float yoffset = 0.0;
          vector<vector <Point> > contours; // Vector for storing contour
          vector<Vec4i> hierarchy;
          vector<vector <Point2f> > rigidsurface;
          vector<vector <Point2f> > ballcenter;
          vector<Point2f> mc;
          Point2f center;
          vector<Point2f> contourshifted;
          double CircularityStandard(const vector<cv::Point> &contour, double contourArea);
          bool isCircle(const vector<cv::Point> &contour, double contourArea);
          float Distance(Point2f& p, Point2f& q);
          bool isRect(const vector<cv::Point> &contour,double contourArea);

};



#endif //MYAPPLICATION2_FINDBALLSANDMAZES_H
