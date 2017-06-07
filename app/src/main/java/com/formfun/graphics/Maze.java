package com.formfun.graphics;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;

import java.util.ArrayList;


/**
 * Created by ROHIT on 23-05-2017.
 */

public class Maze {

    private BoundaryPoints rigidpoints;
    private ArrayList<Vec2> surface;
    private Body body;
    private Createbox2d box2d;
    private int index = 0;


    public Maze(BoundaryPoints rigidpoints,Createbox2d _box2d) //used to create mazes
    {
        box2d = _box2d;
        this.rigidpoints = rigidpoints; //get points to form maze
        surface = new ArrayList<Vec2>(); //to build box2d surface
        for(int i = 0; i < rigidpoints.size(); i++ )
            if(this.rigidpoints.getPoints().get(i).x > 0 && this.rigidpoints.getPoints().get(i).y > 0)
                surface.add(new Vec2(this.rigidpoints.getPoints().get(i).x, this.rigidpoints.getPoints().get(i).y)); //add points to build box2d surface

        if(surface.size() > 0)
        {
            ChainShape chain = new ChainShape(); //chainshape in box2d
            Vec2[] vertices = new Vec2[surface.size()];
            for(int i = 0; i < vertices.length; i++)
            {
                vertices[i] = box2d.coordPixelsToWorld(surface.get(i)); //convert surface points to box2d world coordinates
            }
            chain.createChain(vertices, vertices.length);//create chain
            BodyDef bd = new BodyDef(); //define body
            body = box2d.getWorld().createBody(bd);
            body.createFixture(chain,1);  //body properties
            body.setUserData(this);
        }

    }

    /**
     * display maze
     */

    void display(Canvas c)
    {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.RED);
        Path path = new Path();
        path.moveTo(rigidpoints.getPoints().get(0).x, rigidpoints.getPoints().get(0).y); //join lines
        for (int j = 1; j < rigidpoints.size(); j++)
        {
            path.lineTo(rigidpoints.getPoints().get(j).x, rigidpoints.getPoints().get(j).y);  //join lines
        }
        c.drawPath(path, paint);//draw points
    }


    /**
     *  call to destory box2d body
     */

    void destory()
    {
        box2d.destroyBody(body); //destory body
    }



}
