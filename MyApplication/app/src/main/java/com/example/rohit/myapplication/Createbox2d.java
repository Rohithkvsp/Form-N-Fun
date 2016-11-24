package com.example.rohit.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;


/**
 * Created by ROHIT on 27-10-2016.
 */
public class Createbox2d {

    static final long FPS = 10;
    boolean doSleep = true;
    private static final float TIMESTEP = 1.0f /60f;
    private static final int VELOCITY_ITERATIONS = 10;
    private static final int POSITION_ITERATIONS = 8;
    Vec2 gravity = new Vec2(0.0f, 0.0f);
    //Vec2 gravity = new Vec2(10.0f, 0.0f);
    World world;
    float scaleFactor=150.0f;
    float IscaleFactor=1/scaleFactor;//meter to pixel
    Canvas canvas;
    float width;
    float height;
    public float transX;// = 320.0f;
    public float transY;// = 240.0f;
    //public float scaleFactor;// = 10.0f;
    public float yFlip;// = -1.0f; //flip y coordinate
    private Box2DContactListener contactlistener;

    Createbox2d(Context context)
    {
        world = new World(gravity);
        world.setWarmStarting(true);
        world.setContinuousPhysics(true);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        Log.d("screen size",width+","+height);
        transX = width/2;
        transY = height/2;
        yFlip = -1;

    }


    public void step() {
        float timeStep = 1.0f / 60f;
        world.step(TIMESTEP,VELOCITY_ITERATIONS,POSITION_ITERATIONS);
        world.clearForces();
    }

    public void destroyBody(Body b) {
        world.destroyBody(b);
    }


    public void addGravity()
    {
        world.setGravity(new Vec2(0,-10));

    }

    public Body createBody(BodyDef bd) {

        return world.createBody(bd);
    }


    public Vec2 getBodyPixelCoord(Body b) {
        Transform xf = b.getTransform();//b.getXForm();
        return coordWorldToPixels(xf.p);
    }


    public float scalarPixelsToWorld(float val) {

        return val*IscaleFactor;
    }

    public float scalarWorldToPixels(float val) {

        return val*scaleFactor;
    }

    public void listenForCollisions() {
        contactlistener = new Box2DContactListener();
        world.setContactListener(contactlistener);
    }

    public Vec2 coordPixelsToWorld(float pixelX, float pixelY) {
        float offsetX=width/2;
        float offsetY=height/2;
        float worldX=(pixelX-offsetX)*IscaleFactor;
        float worldY=(-pixelY+offsetY)*IscaleFactor;
        return new Vec2(worldX,worldY);
    }

    public Vec2 coordPixelsToWorld(Vec2 p) {

        float offsetX=width/2;
        float offsetY=height/2;
        float worldX=(p.x-offsetX)*IscaleFactor;
        float worldY=(-p.y+offsetY)*IscaleFactor;
        return new Vec2(worldX,worldY);
    }


    public Vec2 coordWorldToPixels(float worldX, float worldY) {
        float offsetX=width/2*IscaleFactor;
        float offsetY=height/2*IscaleFactor;
        float pixelX=(worldX+offsetX)*scaleFactor;
        float pixelY=(-worldY+offsetY)*scaleFactor;
        return new Vec2(pixelX, pixelY);
    }

    private Vec2 coordWorldToPixels(Vec2 p) {
        float offsetX=width/2*IscaleFactor;
        float offsetY=height/2*IscaleFactor;
        float pixelX=(p.x+offsetX)*scaleFactor;
        float pixelY=(-p.y+offsetY)*scaleFactor;
        return new Vec2(pixelX, pixelY);
    }

}
