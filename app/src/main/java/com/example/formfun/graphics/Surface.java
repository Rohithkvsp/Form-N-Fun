package com.example.formfun.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import java.util.ArrayList;

/**
 * Created by ROHIT on 23-05-2017.
 */

public class Surface
{
    private ArrayList<Vec2> surface;
    private float x0,y0,x1,y1;
    private Body body;
    private Createbox2d box2d;

    public Surface(float _x0,float _y0,float _x1,float _y1 ,Createbox2d _box2d)  //used as boundary for the game so that balls won't go out of the screen
    {
        x0 = _x0;
        y0 = _y0;
        x1 = _x1;
        y1 = _y1;
        box2d = _box2d;
        surface = new ArrayList<Vec2>();
        surface.add(new Vec2(x0,y0));
        surface.add(new Vec2(x1,y1));
        ChainShape chain = new ChainShape(); //create chainshape in box2d
        Vec2[] vertices = new Vec2[surface.size()];
        for(int i = 0; i < vertices.length; i++)
        {
            vertices[i] = box2d.coordPixelsToWorld(surface.get(i)); //convert surface points to box2d world coordinates
        }
        chain.createLoop(vertices, vertices.length);
        BodyDef bd = new BodyDef(); //Define body
        body = box2d.getWorld().createBody(bd);
        body.createFixture(chain,1);
        body.setUserData(this);

    }

    /**
     * display surface
     */
    void display(Canvas c)
    {
        Paint paint = new Paint();
        paint.setColor(0x000000);//make it light
        paint.setAlpha(255);
        paint.setStrokeWidth(6);
        c.drawLine(x0,y0,x1,y1, paint); //drawlines
    }

    /**
     * *  call to destory box2d body
     */
    void destory()
    {
        box2d.destroyBody(body);
    }

}
