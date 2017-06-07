package com.formfun.graphics;

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

/**
 * Created by ROHIT on 23-05-2017.
 */

public class GraphicSurface extends SurfaceView implements SurfaceHolder.Callback {


    public SurfaceHolder holder;
    private GraphicThread graphicThread;
    private boolean toggle = false;
    private float azimuth = 0.0f;
    private float pitch = 0.0f;
    private float roll = 0.0f;

    public GraphicSurface(Context context) {
        super(context);
        graphicThread=new GraphicThread(this,context); //create game thread;
        holder=getHolder(); //surface holder
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
        stopGraphics();

    }


    public void setAddressandScreenSize(long address,int screen_width, int screen_height)
    {
        graphicThread.setAddressandScreenSize(address,screen_width,screen_height);
    }

    /**
     * start game thread
     */

    public void startGraphics()
    {
        graphicThread.setRunning(true);
        graphicThread.start(); //start game thread
        toggle = true;
        Log.v("APP::GraphicSurface","graphics Started");

    }

    /**
     * stop game thread
     */

    public void stopGraphics()
    {
        if(toggle)
        {
            Log.v("APP::GraphicSurface","about to stop graphics");
            boolean retry = true;
            graphicThread.setRunning(false);
            toggle = false;
            while(retry)
            {
                try
                {
                    graphicThread.join();
                    retry = false;
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        graphicThread.destoryAll(); //destroy maze balls and surfaces
        Log.v("APP::GraphicSurface","graphics stopped");
    }


    /**
     * set azimuth
     */
    public void setAzimuth(float _azimuth)
    {
        azimuth = _azimuth;
    }

    /**
     * set pitch
     */
    public void setPitch(float _pitch )
    {
        pitch = _pitch;
    }

    /**
     *set roll
     */
    public void setRoll(float _roll)
    {
        roll = _roll;
    }

    /**
     * return azimuth to game thread
     */
    public float getAzimuth()
    {
        return azimuth;
    }

    /**
     * return pitch to game thread
     */
    public float getPitch()
    {
        return pitch;
    }

    /**
     * return roll to game thread
     */
    public float getRoll()
    {
        return roll;
    }

}