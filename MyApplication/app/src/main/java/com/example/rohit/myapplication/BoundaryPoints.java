package com.example.rohit.myapplication;


/**
 * Created by ROHIT on 23-10-2016.
 */

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class BoundaryPoints {

    ArrayList<PointF> arrlist;
    public BoundaryPoints()
    {
        arrlist=new ArrayList<PointF>();
    }

    public void addPoint(float x,float y)
    {
        arrlist.add(new PointF(x,y));
    }
    public void clear()
    {
        arrlist.clear();
    }
    public int size()
    {
        return  arrlist.size();
    }
    ArrayList<PointF> getPoints()
    {
        return  arrlist;
    }


}
