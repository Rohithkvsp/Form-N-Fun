package com.example.rohit.myapplication;

/**
 * Created by ROHIT on 23-10-2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;

public class GraphicSurface extends SurfaceView implements SurfaceHolder.Callback {


    public SurfaceHolder holder;
    private GraphicThread graphicThread;
    private boolean toggle=false;
    Createbox2d box2d;
    float xAxis;
    float yAxis;
    float zAxis;
    float azimuth = 0.0f;
    float pitch = 0.0f;
    float roll = 0.0f;


    public GraphicSurface(Context context) {
        super(context);
        box2d=new Createbox2d(context);
        box2d.listenForCollisions();
        graphicThread=new GraphicThread(this,context,box2d);
        holder=getHolder();
        holder.addCallback(this);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        stopgraphics();

    }


    public void startgraphics()
    {

        graphicThread.setRunning(true);
        graphicThread.start();
        toggle=true;
        Log.v("APP","graphics Started");

    }

    public void stopgraphics()
    {

        if(toggle)
        {
            Log.v("APP","about to stop graphics");
            boolean retry=true;

            graphicThread.setRunning(false);
            toggle=false;
            while(retry)
            {
                try
                {
                    graphicThread.join();
                    retry=false;

                }
                catch (InterruptedException e) {
                }
            }

        }
        Log.v("APP","graphics stopped");
    }


    public void setXvalue(float _x) {xAxis = _x;}

    public void setYvalue(float _y) {
        yAxis = _y;
    }

    public void setZvalue(float _z) {
        zAxis = _z;
    }

    public float getXvalue()
    {
        return xAxis;
    }
    public float getYvalue()
    {
        return yAxis;
    }
    public float getZvalue()
    {
        return zAxis;
    }



    public void setAzimuth(float _azimuth) {azimuth = _azimuth;}

    public void setPitch(float _pitch ) {pitch = _pitch;}

    public void setRoll(float _roll) {
        roll = _roll;
    }

    public float getAzimuth()
    {
        return azimuth;
    }
    public float getPitch()
    {
        return pitch;
    }
    public float getRoll()
    {
        return roll;
    }



}