package com.example.rohit.myapplication;

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
 * Created by ROHIT on 27-10-2016.
 */
public class Maze {

    BoundaryPoints rigidpoints;
    ArrayList<Vec2> surface;
    //ArrayList<Vec2> refinedsurface;

    Createbox2d box2d;
    Canvas canvas;
    int index=0;
    public Maze(BoundaryPoints rigidpoints,Createbox2d box2dobject){
        this.rigidpoints=rigidpoints;
        box2d=box2dobject;
        surface = new ArrayList<Vec2>();
        for(int i=0;i<rigidpoints.size();i++)
            if(this.rigidpoints.getPoints().get(i).x >0&&this.rigidpoints.getPoints().get(i).y>0)
              surface.add(new Vec2(this.rigidpoints.getPoints().get(i).x , this.rigidpoints.getPoints().get(i).y));
        if(surface.size()>0)
        {
            ChainShape chain=new ChainShape();
            Vec2[] vertices=new Vec2[surface.size()];
            for(int i=0;i<vertices.length;i++)
            {
                vertices[i]=box2d.coordPixelsToWorld(surface.get(i));
            }

            chain.createChain(vertices, vertices.length);
            BodyDef bd=new BodyDef();
            Body body=box2d.world.createBody(bd);
            body.createFixture(chain,1);
            body.setUserData(this);
        }




    }

    void display(Canvas c)
    {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(Color.RED);
            Log.v("App Rigid size",String.valueOf(rigidpoints.size()));
            Path path=new Path();
            path.moveTo(rigidpoints.getPoints().get(0).x ,rigidpoints.getPoints().get(0).y);
            for (int j = 1;j < rigidpoints.size(); j++)
            {


                // Log.v("App path",String.valueOf(points[i].getPoints().get(j).x+","+points[i].getPoints().get(j).y));
                path.lineTo(rigidpoints.getPoints().get(j).x,rigidpoints.getPoints().get(j).y);
            }
            c.drawPath(path, paint);
    }


}
