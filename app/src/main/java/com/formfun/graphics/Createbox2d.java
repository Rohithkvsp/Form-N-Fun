package com.formfun.graphics;

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
 * Created by ROHIT on 23-05-2017.
 */

public class Createbox2d {

    static final long FPS = 10;
    boolean doSleep = true;
    private static final float TIMESTEP = 1.0f /60f;
    private static final int VELOCITY_ITERATIONS = 10;
    private static final int POSITION_ITERATIONS = 8;
    private Vec2 gravity = new Vec2(0.0f, 0.0f);
    //Vec2 gravity = new Vec2(10.0f, 0.0f);
    private World world;
    private float scaleFactor = 150.0f;
    private float IscaleFactor=1/scaleFactor;//meter to pixel
    private Canvas canvas;
    private float width;
    private float  height;
    private Box2DContactListener contactlistener;

    Createbox2d()
    {
        world = new World(gravity);
        world.setWarmStarting(true);
        world.setContinuousPhysics(true);
    }

    public void setScreenSize(float _width, float _height)
    {
        width = _width;
        height = _height;
        Log.v("createbox2d","screen size: "+String.valueOf(width)+" , "+String.valueOf(height));
    }

    /**
     * to iterate in box2d world
     */
    public void step() {
        float timeStep = 1.0f / 60f;
        world.step(TIMESTEP,VELOCITY_ITERATIONS,POSITION_ITERATIONS);
        world.clearForces();
    }
    /**
     * destory body in world
     */

    public void destroyBody(Body b) {
        world.destroyBody(b);
    }

    /**
     *  add gravity
     */

    public void addGravity()
    {
        world.setGravity(new Vec2(0,-10));

    }

    /**
     *  create body in box2d world
     */

    public Body createBody(BodyDef bd) {

        return world.createBody(bd);
    }
    /**
     *  returns world object
     */
    public World getWorld() {
        return world;
    }

    /**
     *  returns body coordinates in pixels
     */
    public Vec2 getBodyPixelCoord(Body b) {
        Transform xf = b.getTransform();//body pixels in box2d world
        return coordWorldToPixels(xf.p);// box2d world to pixels
    }

    /**
     *  scale pixesls to world
     */

    public float scalarPixelsToWorld(float val) {

        return val*IscaleFactor;
    }

    /**
     *  scale world to pixesl
     */

    public float scalarWorldToPixels(float val) {

        return val*scaleFactor;
    }

    /**
     *  box2d contact listener
     */

    public void listenForCollisions(Context context) {
        contactlistener = new Box2DContactListener(context);
        world.setContactListener(contactlistener);
    }

    /**
     *   pixels (x,y) to box2d world
     */
    public Vec2 coordPixelsToWorld(float pixelX, float pixelY) {
        float offsetX=width/2;
        float offsetY=height/2;
        float worldX=(pixelX-offsetX)*IscaleFactor;
        float worldY=(-pixelY+offsetY)*IscaleFactor;
        return new Vec2(worldX,worldY);
    }

    /**
     *  pixels (Vec2) to box2d world
     */

    public Vec2 coordPixelsToWorld(Vec2 p) {

        float offsetX=width/2;
        float offsetY=height/2;
        float worldX=(p.x-offsetX)*IscaleFactor;
        float worldY=(-p.y+offsetY)*IscaleFactor;
        return new Vec2(worldX,worldY);
    }

    /**
     *  box2d world (x,y) to pixels
     */

    public Vec2 coordWorldToPixels(float worldX, float worldY) {
        float offsetX=width/2*IscaleFactor;
        float offsetY=height/2*IscaleFactor;
        float pixelX=(worldX+offsetX)*scaleFactor;
        float pixelY=(-worldY+offsetY)*scaleFactor;
        return new Vec2(pixelX, pixelY);
    }

    /**
     *   box2d world (Vec2) to pixels
     */

    public Vec2 coordWorldToPixels(Vec2 p) {
        float offsetX=width/2*IscaleFactor;
        float offsetY=height/2*IscaleFactor;
        float pixelX=(p.x+offsetX)*scaleFactor;
        float pixelY=(-p.y+offsetY)*scaleFactor;
        return new Vec2(pixelX, pixelY);
    }
}
