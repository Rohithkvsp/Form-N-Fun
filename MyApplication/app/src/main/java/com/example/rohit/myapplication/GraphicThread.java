package com.example.rohit.myapplication;

/**
 * Created by ROHIT on 23-10-2016.
 */


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

public class GraphicThread extends Thread {

    private boolean running = false;
    private final int refresh_rate=6;
    GraphicSurface Gs;
    BoundaryPoints[] rigidpoints;
    BoundaryPoints[] ballpoints;
    //BoundaryPoints[] hullpoints;
    Canvas c = null;
    Createbox2d box2d;
    Context context;
    private  ArrayList<Ball>balls=new ArrayList<Ball>();

    private  ArrayList<Maze>mazes=new ArrayList<Maze>();
    private Surface s1,s2,s3,s4;
    float screenheight,screenwidth;

    public GraphicThread(GraphicSurface Gs,Context context_,Createbox2d box2dobject_)
    {
        this.Gs=Gs;
        box2d=box2dobject_;
        context=context_;
        Log.v("APP",String.valueOf("gs x "+Gs.getXvalue()+" , gs y "+Gs.getYvalue()));
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @SuppressLint("NewApi")
    public void run() {
        long previousTime, currentTime;
        previousTime = System.currentTimeMillis();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenwidth = size.x;
        screenheight = size.y;

        s1=new Surface(5,5,screenwidth-5,5,box2d);
        s2=new Surface(screenwidth-5,5,screenwidth-5,screenheight-5,box2d);
        s3=new Surface(screenwidth-5,screenheight-5,5,screenheight-5,box2d);
        s4=new Surface(5,screenheight-5,5,5,box2d);

        rigidpoints= getrigidsurface();

        for(int i=0;i<rigidpoints.length;i++)
        {Log.v("App maze size",String.valueOf(rigidpoints[i].size()));
          mazes.add(new Maze(rigidpoints[i],box2d));
        }

        ballpoints=getballPoints();

        for(int i=0;i<ballpoints.length;i++)
        {
            Log.v("App Circle size",String.valueOf(ballpoints[i].size()));
            balls.add(new Ball(ballpoints[i].getPoints().get(0).x, ballpoints[i].getPoints().get(0).y,ballpoints[i].getPoints().get(1).x ,box2d));
        }

        while(running) {
          //  Canvas c = null;

            currentTime = System.currentTimeMillis();
            while ((currentTime - previousTime) < refresh_rate) {
                currentTime = System.currentTimeMillis();
            }
            previousTime = currentTime;

            box2d.step();
            for(Ball ball:balls)
            {
                Log.v("APP",String.valueOf("force gs x "+Gs.getXvalue()+" , gs y "+Gs.getYvalue()));
                ball.addforce(Gs.getXvalue(), Gs.getYvalue());
            }



            try
            {
                c=Gs.getHolder().lockCanvas();
                synchronized(Gs.getHolder())
                {

                    c.drawColor(Color.TRANSPARENT);
                    c.drawColor(0,Mode.CLEAR);
                    s1.display(c);
                    s2.display(c);
                    s3.display(c);
                    s4.display(c);
                    for(Maze maze:mazes) {
                        maze.display(c);
                    }

                    for(Ball ball:balls)
                    {
                        ball.display(c);
                    }



                    /*Paint paint = new Paint();
                    paint.setColor(Color.BLUE);
                    rigidpoints= getrigidsurface();
                    for(int i=0;i<rigidpoints.length;i++)
                    {
                        Log.v("App Rigid size",String.valueOf(rigidpoints[i].size()));
                        Path path=new Path();
                        path.moveTo(rigidpoints[i].getPoints().get(0).x , rigidpoints[i].getPoints().get(0).y);
                        for (int j = 1;j < rigidpoints[i].getPoints().size(); j++)
                        {
                           // Log.v("App path",String.valueOf(points[i].getPoints().get(j).x+","+points[i].getPoints().get(j).y));
                            path.lineTo(rigidpoints[i].getPoints().get(j).x, rigidpoints[i].getPoints().get(j).y);
                        }
                        c.drawPath(path, paint);
                    }

                   Paint paint1 = new Paint();
                    paint.setColor(Color.GREEN);

                    ballpoints=getballPoints();
                    for(int i=0;i<ballpoints.length;i++)
                    {
                        Log.v("App Circle size",String.valueOf(ballpoints[i].size()));
                        c.drawCircle(ballpoints[i].getPoints().get(0).x, ballpoints[i].getPoints().get(0).y,ballpoints[i].getPoints().get(1).x , paint1);
                    }

                    hullpoints=gethullPoints();
                    Paint paint2 = new Paint();
                    paint2.setColor(Color.WHITE);
                    for(int i=0;i<hullpoints.length;i++)
                    {
                        Log.v("App hull size",String.valueOf(hullpoints[i].size()));
                        Path path=new Path();
                        path.moveTo(hullpoints[i].getPoints().get(0).x , hullpoints[i].getPoints().get(0).y);
                        for (int j = 1;j < hullpoints[i].getPoints().size(); j++)
                        {
                          // Log.v("App path",String.valueOf(points[i].getPoints().get(j).x+","+points[i].getPoints().get(j).y));
                            path.lineTo(hullpoints[i].getPoints().get(j).x, hullpoints[i].getPoints().get(j).y);
                        }
                        paint2.setStyle(Paint.Style.STROKE);
                        c.drawPath(path, paint2);
                    }

                    */

                }

            }
            finally
            {

            }
            if (c != null) {
                Gs.getHolder().unlockCanvasAndPost(c);
            }
        }
        try {
            Thread.sleep(refresh_rate-5); // Wait some time till I need to display again
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }




    public native BoundaryPoints[] getrigidsurface();
    public native BoundaryPoints[] getballPoints();
    //public native BoundaryPoints[] gethullPoints();
}
