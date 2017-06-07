package com.formfun.graphics;

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
 * Created by ROHIT on 23-05-2017.
 */

public class Ball {

    private float x,y,r;
    private Createbox2d box2d;
    private Body b1;

    public Ball(float _x,float _y,float _r, Createbox2d _box2d) //used to create balls
    {
        x = _x;
        y = _y;
        r = _r;
        box2d = _box2d;
        BodyDef bd = new BodyDef();// define body
        bd.type = BodyType.DYNAMIC; //make body dynamic
        bd.position.set(box2d.coordPixelsToWorld(x,y)); //intial position to ball center
        b1 = box2d.createBody(bd); //create body
        CircleShape cs = new CircleShape(); //create box2d circle shape
        cs.m_radius = box2d.scalarPixelsToWorld(r);//cricle radius to ball radius
        FixtureDef fd = new FixtureDef();// body properites
        fd.shape = cs;
        fd.density = 1; //density to 1
        fd.friction = 0.2f;//0.2f;//0.0f;
        fd.restitution = 0.5f;//0.7f;//1.0f;
        b1.createFixture(fd); //add properties to body
        b1.setUserData(this);
    }


    /**
     * display ball
     */

    public void display(Canvas c)
    {
        Vec2 pos = box2d.getBodyPixelCoord(b1); //get body center
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        c.drawCircle(pos.x, pos.y,r, paint); //draw ball
        Vec2 center = b1.getWorldCenter();
        b1.applyForce(new Vec2(0,0), center); //add force on center
    }

    /**
     * add force on center of the ball
     */

    public void addforce(float x, float y)
    {
        Vec2 force = new Vec2(b1.getMass()*y/2,-b1.getMass()*x/2); //create force
        Vec2 pos = b1.getWorldCenter();
        b1.applyForce(force, pos);//apply force on body center
    }


    /**
     *  call to destory box2d body
     */
    void destory()
    {
        box2d.destroyBody(b1);
    }


}
