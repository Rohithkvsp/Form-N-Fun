package com.example.rohit.myapplication;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Created by ROHIT on 27-10-2016.
 */
public class Box2DContactListener implements ContactListener {

    @Override
    public void beginContact(Contact cp) {
        // TODO Auto-generated method stub
        Fixture f1 = cp.getFixtureA();
        Fixture f2 = cp.getFixtureB();
        // Get both bodies
        Body b1 = f1.getBody();
        Body b2 = f2.getBody();
        // Get our objects that reference these bodies

        Object o1 = b1.getUserData();
        Object o2 = b2.getUserData();




        if ((o1.getClass() == Maze.class&&o2.getClass() == Ball.class)||(o1.getClass() == Ball.class&&o2.getClass() == Maze.class)) {
            balltosurfacecontact();
        }

    }

    @Override
    public void endContact(Contact arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postSolve(Contact arg0, ContactImpulse arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void preSolve(Contact arg0, Manifold arg1) {
        // TODO Auto-generated method stub

    }

    public void balltoballcontact()
    {
        MainActivity.v.vibrate(0);

    }

    public void balltosurfacecontact()
    {
        //MainActivity.v.vibrate(1);
        MainActivity.soundpool.play(MainActivity.sound_id, 1, 1, 1, 0, (float) 0.5);


    }


}
