package com.example.rohit.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.view.SurfaceView;
import android.widget.Button;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.JavaCameraView;
import android.graphics.PixelFormat;
import android.hardware.Camera.Size;

import android.content.Context;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.hardware.Camera;

import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.CvType;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;




public class MainActivity extends Activity implements OnClickListener, CvCameraViewListener2 {


    ///private CameraBridgeViewBase mOpenCvCameraView;
    private ViewJavaCamera mOpenCvCameraView;
    private static final String TAG = "APP::Activity";
    private int mCameraIndex=0;
    private Mat mRgba;
    private Mat mProcessed;
    public static int matheight;
    public static int matwidth;
    public static int screenheight=-1;
    public static int screenwidth=-1;


    private Detect detect;
    private Button bt,bt2;
    private boolean PressedOnce = true;
    private GraphicSurface gs;
    private int accelerometerSensor;
    private int magneticSensor;
    private float xAxis;
    private float yAxis;
    private float zAxis;
    private SensorManager sensorManager;
    public static Vibrator v;
    public static SoundPool soundpool;
    public static int sound_id ;

    float[] InR = new float[16];
    float[] I = new float[16];
    float[] gravity = new float[3];
    float[] geomag = new float[3];
    float[] orientVals = new float[3];

    float azimuth = 0.0f;
    float pitch = 0.0f;
    float roll = 0.0f;



    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("process");

        }
    }




    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch(status)
            {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");


                    mOpenCvCameraView.enableView();

                    screenwidth = mOpenCvCameraView.getWidth();
                    screenheight = mOpenCvCameraView.getHeight();

                    detect = new Detect(screenwidth, screenheight);
                }break;
                default:
                {
                    super.onManagerConnected(status);
                } break;



            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FrameLayout layout = new FrameLayout(this);
        layout.setLayoutParams(new FrameLayout.LayoutParams(  FrameLayout.LayoutParams.MATCH_PARENT,  FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(layout);

        mOpenCvCameraView = new ViewJavaCamera(this,0 );

        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));


        layout.addView(mOpenCvCameraView);
        gs=new GraphicSurface(this);
        gs.getHolder().setFormat( PixelFormat.TRANSPARENT);
        gs.setZOrderOnTop(true);
        gs.setLayoutParams(new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        layout.addView(gs);
        bt=new Button(this);
        bt.setText("Play");
        bt.setId(12345);
        bt.getBackground().setAlpha(64);
        bt.setOnClickListener(this);
        bt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,Gravity.CENTER));
        layout.addView(bt);
        //bt2=new Button(this);
        // bt2.setText("Solve");
        //bt2.setId(23456);
        //bt2.getBackground().setAlpha(64);
        //bt2.setOnClickListener(this);
        //bt2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        //layout.addView(bt2);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = Sensor.TYPE_ACCELEROMETER;
        magneticSensor = Sensor.TYPE_MAGNETIC_FIELD;
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(magneticSensor),SensorManager.SENSOR_DELAY_NORMAL);

        v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_id= soundpool.load(getBaseContext(), R.raw.bounce,1);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case 12345:
                if(PressedOnce) {
                    mOpenCvCameraView.disableView();
                    bt.setText("Detect");
                    PressedOnce=false;

                    gs.startgraphics( );

                }
                else
                {
                    recreate();
                    //mOpenCvCameraView.enableView();
                    //bt.setText("Play");
                    PressedOnce=true;
                   // gs.stopgraphics();
                }
                break;
            case 23456:
                break;
        }

    }


    final SensorEventListener sensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged (Sensor senor, int accuracy) {
            //Not used
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // TODO Auto-generated method stub
            /*if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                xAxis = sensorEvent.values[0];
                yAxis = sensorEvent.values[1];
                zAxis = sensorEvent.values[2];
             //   Log.v("TAG","xAxis: "+ String.valueOf(xAxis)+" yAxis: "+String.valueOf(yAxis)+" zAxis: "+String.valueOf(zAxis));
                gs.setXvalue(xAxis);
                gs.setYvalue(yAxis);
                gs.setZvalue(zAxis);


            }*/


            if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
                return;

            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    gravity = sensorEvent.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    geomag = sensorEvent.values.clone();
                    break;
            }

            if (gravity != null && geomag != null) {

                boolean success = SensorManager.getRotationMatrix(InR, I,
                        gravity, geomag);
                if (success) {
                    SensorManager.getOrientation(InR, orientVals);
                    azimuth = (float) Math.toDegrees(orientVals[0]);
                    pitch = (float) Math.toDegrees(orientVals[1]);
                    roll = (float) Math.toDegrees(orientVals[2]);
                    gs.setAzimuth(azimuth);
                    gs.setPitch(pitch);
                    gs.setRoll(roll);

                    Log.v("TAG","azimuth: "+ String.valueOf(azimuth)+" pitch: "+String.valueOf(pitch)+" roll: "+String.valueOf(roll));

                }
            }

        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(magneticSensor), SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        if(gs!=null)
            gs.surfaceDestroyed(gs.holder);
        sensorManager.unregisterListener(sensorEventListener);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.v("TAG","menu created");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onCameraViewStarted(int width, int height) {
        List<Size> mResolutionList = mOpenCvCameraView.getResolutionList();
        Size mSize = null;
        for (Size size : mResolutionList) {
            Log.i(TAG, "Available resolution: "+size.width+" "+size.height);
             if(size.width<=1280&&size.height<=980) { //1280x980,1440x1080
                    mSize = size;
                    Log.i(TAG, "selected resolution: "+size.width+" "+size.height);
                    break;
                }
            }
        mOpenCvCameraView.setResolution(mSize);
        Size resolution = mOpenCvCameraView.getResolution();
        String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
        Log.v("Resolution ",caption);


        mRgba = new Mat();
        mProcessed= new Mat();

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba=inputFrame.rgba();
        if (android.os.Build.MODEL.equalsIgnoreCase("Nexus 5X"))
            Core.flip(mRgba, mRgba,-1);
        detect.process(mRgba,mProcessed);
        return mProcessed;
    }


}
