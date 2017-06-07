package com.formfun.graphics;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import android.content.Context;

import android.media.AudioManager;
import android.media.SoundPool;
import com.formfun.R;


/**
 * Created by ROHIT on 23-05-2017.
 */

public class Box2DContactListener implements ContactListener {

    private static SoundPool soundpool;
    private static int sound_id;

    public Box2DContactListener(Context context_)
    {
        soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_id = soundpool.load(context_, R.raw.bounce,1);
    }

    @Override
    public void beginContact(Contact cp) {
        // TODO Auto-generated method stub
        Fixture f1 = cp.getFixtureA();
        Fixture f2 = cp.getFixtureB();

        // Get both bodies
        Body b1 = f1.getBody();
        Body b2 = f2.getBody();

        // Get objects that reference these bodies
        Object o1 = b1.getUserData();
        Object o2 = b2.getUserData();

        if ((o1.getClass() == Maze.class && o2.getClass() == Ball.class) || (o1.getClass() == Ball.class && o2.getClass() == Maze.class)) { //if ball touches maze
            balltosurfacecontact(); //play sound
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


    public void balltosurfacecontact()
    {
      soundpool.play(sound_id, 1, 1, 1, 0, (float) 0.5);
    }


}
