package com.formfun;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;

import java.util.List;

/**
 * Created by ROHIT on 21-05-2017.
 */
public class CameraView extends JavaCameraView {


    public CameraView(Context context, int cameraId) {
        super(context,cameraId);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public List<Camera.Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }
    /**
     * calls disconnectCamera() to stop the camera processing thread
     */

    void stopCamera()
    {
        disconnectCamera();//stop the thread
    }

    /**
     * change the resolution of camera
     */
    public void setResolution(Camera.Size resolution) {
        disconnectCamera(); //disconnect camera
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight()); //connect camera

    }
    /**
     * get different camera preview sizes
     */

    public Camera.Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }


}
