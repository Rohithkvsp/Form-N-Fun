package com.example.formfun;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.graphics.PixelFormat;
import android.view.SurfaceView;
import android.view.View;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;
import android.widget.Button;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.Size;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.List;

import com.example.formfun.graphics.GraphicSurface;


public class MainActivity extends AppCompatActivity implements OnClickListener, CvCameraViewListener2 {

    private CameraView mOpenCvCameraView;
    private static final String TAG = "APP::Activity";
    private int mCameraIndex = 0;
    private Mat mRgba;
    private int CAMERA = 10;

    private FindMazesAndBalls findmazesandballs;

    private Button bt;
    private GraphicSurface gs;
    private boolean PressedOnce = true;

    private int accelerometerSensor;
    private int magneticSensor;
    private SensorManager sensorManager;

    private float[] InR = new float[16];
    private float[] I = new float[16];
    private float[] gravity = new float[3];
    private float[] geomag = new float[3];
    private float[] orientVals = new float[3];
    private float azimuth = 0.0f;
    private float pitch = 0.0f;
    private float roll = 0.0f;

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
        layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(layout);

        mOpenCvCameraView = new CameraView(this,mCameraIndex); //mCameraIndex = 0 indicates rear camera
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        layout.addView(mOpenCvCameraView);  //add CameraView to frame layout
        gs = new GraphicSurface(this); // create graphic surface
        gs.getHolder().setFormat( PixelFormat.TRANSPARENT); //set graphic surface to transparent
        gs.setZOrderOnTop(true); //graphic surface as top layer
        gs.setLayoutParams(new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        layout.addView(gs); //add graphic surface  to frame layout
        bt = new Button(this); // create button
        bt.setText("Play");
        bt.setId(12345);
        bt.getBackground().setAlpha(64); //button to transparent
        bt.setOnClickListener(this);
        bt.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        layout.addView(bt); //add button to frame layout
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = Sensor.TYPE_ACCELEROMETER; //accelerometer
        magneticSensor = Sensor.TYPE_MAGNETIC_FIELD; //magentic sensor
        if(Build.VERSION.SDK_INT >= 23)
            askForPermission(Manifest.permission.CAMERA,CAMERA); //ask for camera permission


    }




    final SensorEventListener sensorEventListener = new SensorEventListener() {

        public void onAccuracyChanged (Sensor senor, int accuracy) {
            //Not used
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                gravity = sensorEvent.values;
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                geomag = sensorEvent.values;
            if(gravity==null)
                Log.v(TAG,"No accelerometer values");
            if(geomag==null)
                Log.v(TAG,"No magnetic field");


            if (gravity != null && geomag != null) {
                boolean success = SensorManager.getRotationMatrix(InR, I, gravity, geomag);
                if (success) {
                    SensorManager.getOrientation(InR, orientVals); //get orientation values
                    azimuth = (float) Math.toDegrees(orientVals[0]);
                    pitch = (float) Math.toDegrees(orientVals[1]);
                    roll = (float) Math.toDegrees(orientVals[2]);
                    gs.setAzimuth(azimuth);
                    gs.setPitch(pitch);
                    gs.setRoll(roll);
                    Log.v(TAG,"azimuth: "+ String.valueOf(azimuth)+" pitch: "+String.valueOf(pitch)+" roll: "+String.valueOf(roll));

                }
            }
        }
    };


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else{

            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case 12345:
                if(PressedOnce)
                { //when pressed
                    if(findmazesandballs.foundbothlineandball()) //if both mazes and balls are found
                    {
                        mOpenCvCameraView.disableView(); //disable camera
                        mOpenCvCameraView.stopCamera();
                        bt.setText("Restart"); ///change button text to Restart
                        PressedOnce = false;
                        if(sensorManager != null)  //register acclerometer and  magnetometer sensors to find orientations
                        {
                            sensorManager.unregisterListener(sensorEventListener);
                            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(magneticSensor), SensorManager.SENSOR_DELAY_NORMAL);
                            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);
                        }
                        if(findmazesandballs != null && findmazesandballs.getselfAddr() != 0)
                            gs.startGraphics(); //start graphics
                    }

                }
                else
                {
                    recreate();  //recreate the activity
                    PressedOnce=true;
                }
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(Build.VERSION.SDK_INT< 23)
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
        hideSystemUI(); //hide UI
    }

    @Override
    public void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        if(gs!=null) {
            gs.surfaceDestroyed(gs.holder);
            gs = null;
        }
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        if(findmazesandballs != null) {
            findmazesandballs.delete();  //delete instance
            findmazesandballs = null;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
        if(mOpenCvCameraView!=null)
            mOpenCvCameraView.disableView();

        if(gs!=null) {
            gs.surfaceDestroyed(gs.holder);
        }

    }



    @Override
    public void onCameraViewStarted(int width, int height) {

        String caption = Integer.valueOf(mOpenCvCameraView.getWidth()).toString() + "x" + Integer.valueOf(mOpenCvCameraView.getHeight()).toString();
        Log.v("screen_size ",caption);
        List<Camera.Size> mResolutionList = mOpenCvCameraView.getResolutionList();
        Camera.Size mSize = null;
        for (Camera.Size size : mResolutionList) {
            Log.i(TAG, "Available resolution: "+size.width+" "+size.height);
            if(size.width<=1280&&size.height<=960) { //change the resolution, 1280x960 and 1440x1080 are working fine in nexus 5x
                mSize = size;
                Log.i(TAG, "selected resolution: "+size.width+" "+size.height);
                break;
            }
        }
        mOpenCvCameraView.setResolution(mSize); //set resolution
        Size resolution = mOpenCvCameraView.getResolution();
        mRgba = new Mat();
        //initialize findmazesandballs
        findmazesandballs = new FindMazesAndBalls(mOpenCvCameraView.getWidth(), mOpenCvCameraView.getHeight(),resolution.width, resolution.height);
        if(findmazesandballs.getselfAddr()!=0)
            gs.setAddressandScreenSize(findmazesandballs.getselfAddr(),mOpenCvCameraView.getWidth(),mOpenCvCameraView.getHeight());
    }


    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba=inputFrame.rgba();
        if (Build.MODEL.equalsIgnoreCase("Nexus 5X")) //flip the frame on nexus5x
            Core.flip(mRgba, mRgba,-1);
        findmazesandballs.apply(mRgba); //process frames
        return mRgba;
    }

    private void hideSystemUI() {
        int windowVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            windowVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }

        getWindow().getDecorView().setSystemUiVisibility(windowVisibility);
    }

}