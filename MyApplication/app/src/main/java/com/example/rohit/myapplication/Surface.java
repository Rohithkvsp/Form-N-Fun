package com.example.rohit.myapplication;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import java.util.ArrayList;

/**
 * Created by ROHIT on 27-10-2016.
 */
public class Surface {

    ArrayList<Vec2> surface;
    float x0,y0,x1,y1;
    Createbox2d box2d;
    Canvas canvas;
    public Surface(float _x0,float _y0,float _x1,float _y1 ,Createbox2d box2dobject)
    {
        x0=_x0;
        y0=_y0;
        x1=_x1;
        y1=_y1;
        box2d=box2dobject;
        surface = new ArrayList<Vec2>();
        surface.add(new Vec2(x0,y0));
        surface.add(new Vec2(x1,y1));

        ChainShape chain=new ChainShape();
        Vec2[] vertices=new Vec2[surface.size()];
        for(int i=0;i<vertices.length;i++)
        {
            vertices[i]=box2d.coordPixelsToWorld(surface.get(i));
        }
       // chain.createChain(vertices, vertices.length);
        chain.createLoop(vertices, vertices.length);
        BodyDef bd=new BodyDef();
        Body body=box2d.world.createBody(bd);
        body.createFixture(chain,1);
        body.setUserData(this);

    }
    void display(Canvas c)
    {
        canvas=c;
        Paint paint = new Paint();
        paint.setColor(0x000000);
        paint.setAlpha(255);
        paint.setStrokeWidth(6);
        canvas.drawLine(x0,y0,x1,y1, paint);
    }

}
