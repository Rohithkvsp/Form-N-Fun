package com.formfun.graphics;

import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Created by ROHIT on 22-05-2017.
 */
public class BoundaryPoints {

    ArrayList<PointF> arrlist;
    public BoundaryPoints()
    {
        arrlist = new ArrayList<PointF>();  //arraylist of android PointF
    }

    public void addPoint(float x,float y)
    {
        arrlist.add(new PointF(x,y)); //add point to arraylist
    }
    public void clear()
    {
        arrlist.clear(); //clear arraylist
    }
    public int size()
    {
        return  arrlist.size(); //return size of arraylist
    }
    ArrayList<PointF> getPoints()
    {
        return  arrlist; //return arraylist
    }

}
