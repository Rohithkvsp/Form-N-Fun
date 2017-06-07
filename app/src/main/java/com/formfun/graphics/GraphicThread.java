package com.formfun.graphics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import java.util.ArrayList;

/**
 * Created by ROHIT on 23-05-2017.
 */

public class GraphicThread extends Thread {

    private boolean running = false;
    private final int refresh_rate=6;

    static {
        System.loadLibrary("process");
    }

    private GraphicSurface Gs;
    private ArrayList<BoundaryPoints> mazesarraylist;
    private ArrayList<BoundaryPoints> ballsarraylist;

    private Canvas c = null;
    private Createbox2d box2d;
    private  ArrayList<Ball>balls=new ArrayList<Ball>();
    private  ArrayList<Maze>mazes=new ArrayList<Maze>();
    private Surface s1,s2,s3,s4;
    private float screenheight,screenwidth;
    private float MIN = -3.8f;
    private float MAX = 3.8f;
    private long objectaddress;


    public GraphicThread(GraphicSurface Gs ,Context context) {
        this.Gs = Gs;
        box2d=new Createbox2d();// create box2d world
        box2d.listenForCollisions(context); // box2d collision listener
    }

    public void setRunning(boolean run) {
        running = run;
    }

    public void setAddressandScreenSize(long address,float screen_width, float screen_height ) {
        objectaddress = address;
        screenwidth = screen_width;
        screenheight = screen_height;
        box2d.setScreenSize(screen_width,screen_height);

    }

 /*
    limits the applied force to MIN and MAX values
  */
   public float limit(float value) {
        return Math.max(MIN, Math.min(value, MAX));
    }

    @SuppressLint("NewApi")
    public void run() {
        long previousTime, currentTime;
        previousTime = System.currentTimeMillis();

        //border surfaces so that ball won't go out of the screen
        s1=new Surface(0,0,screenwidth,0,box2d);
        s2=new Surface(screenwidth,0,screenwidth,screenheight,box2d);
        s3=new Surface(screenwidth,screenheight,0,screenheight,box2d);
        s4=new Surface(0,screenheight,0,0,box2d);

        mazesarraylist = getmazesarraylist(objectaddress); // jni call to get maze points

        if(mazesarraylist!=null)
            for(int i=0;i<mazesarraylist.size();i++)
            {
                mazes.add(new Maze(mazesarraylist.get(i),box2d)); //add maze points to maze for creating box2d body
            }

        ballsarraylist = getballsarraylist(objectaddress); // jni call to get ball points

        if(ballsarraylist!=null)
            for(int i=0;i<ballsarraylist.size();i++)
            {
                //add ball points to ball for creating box2d body
                balls.add(new Ball(ballsarraylist.get(i).getPoints().get(0).x, ballsarraylist.get(i).getPoints().get(0).y,ballsarraylist.get(i).getPoints().get(1).x ,box2d));
            }

        while(running) {
            //  Canvas c = null;

            currentTime = System.currentTimeMillis();
            while ((currentTime - previousTime) < refresh_rate) {
                currentTime = System.currentTimeMillis();
            }
            previousTime = currentTime;

            box2d.step(); //box2d step
            for(Ball ball:balls)
            {
                Log.v("APP",String.valueOf("force gs roll "+limit(Gs.getRoll())+" , gs picth "+limit(Gs.getRoll())));
                /// ball.addforce(Gs.getXvalue(), Gs.getYvalue());
                ball.addforce(-limit(Gs.getRoll()), -limit(Gs.getPitch())); // add force on ball and limit it
            }



            try
            {
                c=Gs.getHolder().lockCanvas();
                synchronized(Gs.getHolder())
                {
                    c.drawColor(Color.TRANSPARENT);
                    c.drawColor(0,Mode.CLEAR);
                    //display border
                    s1.display(c);
                    s2.display(c);
                    s3.display(c);
                    s4.display(c);
                    for(Maze maze:mazes) {
                        maze.display(c);  //display maze
                    }

                    for(Ball ball:balls)
                    {
                        ball.display(c); //bisplay balls
                    }
                }
            }
            finally
            {
                if (c != null) {
                    Gs.getHolder().unlockCanvasAndPost(c);
                }
            }

        }
        try {
            Thread.sleep(refresh_rate-5); // Wait some time till I need to display again
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    void destoryAll()
    {
        if(s1 != null)
            s1.destory();
        if(s2 != null)
            s2.destory();
        if(s3 != null)
            s3.destory();
        if(s4 != null)
            s4.destory();

        for(Maze maze:mazes) {
            maze.destory();  //display maze
        }

        for(Ball ball:balls)
        {
            ball.destory(); //bisplay balls
        }

    }


    private static native ArrayList<BoundaryPoints> getmazesarraylist(long selfAddr);
    private static native ArrayList<BoundaryPoints> getballsarraylist(long selfAddr);
}
