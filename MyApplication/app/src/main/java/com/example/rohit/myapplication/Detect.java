package com.example.rohit.myapplication;

/**
 * Created by ROHIT on 04-10-2016.
 */
import android.graphics.Point;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;



public class Detect {
    int screenwidth=-1;
    int screenheight=-1;
    public Detect(int sw,int sh)
    {
        screenwidth=sw;
        screenheight=sh;
    }
    public void process(Mat mRgba, Mat mProcessed)
    {

        process(mRgba.getNativeObjAddr(), mProcessed.getNativeObjAddr(),screenwidth,screenheight);
        /*for(int i=0;i<getCircle().length;i++)
        {
            Log.v("App circle size",String.valueOf(getCircle()[i].size()));
        }
        */


    }
    public void noprocess(Mat mRgba, Mat mProcessed)
    {

        mRgba.copyTo(mProcessed);
    }

    public native int process(long matAddrRgba, long matAddrGray ,int screenwidth,int screenheight);
    public native BoundaryPoints[] getCircle();

}
