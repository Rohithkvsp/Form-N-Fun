package com.example.rohit.myapplication;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.Log;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Created by ROHIT on 27-10-2016.
 */
public class Ball {


    float x,y,r;
    Createbox2d box2d;
    Body b1;
    public Ball(float _x,float _y,float _r, Createbox2d box2dobject)
    {
        x=_x;
        y=_y;
        r=_r;
        box2d=box2dobject;

        BodyDef bd=new BodyDef();
        bd.type= BodyType.DYNAMIC;
        bd.position.set(box2d.coordPixelsToWorld(x,y));
        b1=box2d.createBody(bd);

        CircleShape cs = new CircleShape();
        cs.m_radius=box2d.scalarPixelsToWorld(r);
        FixtureDef fd=new FixtureDef();
        fd.shape=cs;
        fd.density=1;
        fd.friction=0.2f;//0.2f;//0.0f;
        fd.restitution=0.5f;//0.7f;//1.0f;
        b1.createFixture(fd);
        b1.setUserData(this);

    }

    public void display(Canvas c)
    {
        Vec2 pos=box2d.getBodyPixelCoord(b1);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        c.drawCircle(pos.x, pos.y,r, paint);
        Vec2 center=b1.getWorldCenter();
        b1.applyForce(new Vec2(0,0), center);

    }

    public void addforce(float x, float y)
    {
        //Log.v("APP","raduis "+r);

        Vec2 force=new Vec2(b1.getMass()*y/2,-b1.getMass()*x/2);
        Vec2 pos=b1.getWorldCenter();
        b1.applyForce(force, pos);
    }



}
