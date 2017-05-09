package com.example.rohit.myapplication;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import java.util.List;
import android.util.Log;

/**
 * Created by ROHIT on 08-05-2017.
 */
public class ViewJavaCamera extends JavaCameraView {

    public ViewJavaCamera(Context context,int cameraId) {
        super(context,cameraId);
    }

    public ViewJavaCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

}
