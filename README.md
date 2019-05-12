# Form-N-Fun
[Form ‘N’ Fun](https://experiments.withgoogle.com/form-n-fun) is an android maze game based on real time computer vision. The uniqueness of this game is that the user can draw his own mazes on a white paper with pen/pencil and play. The game detects the maze and user can play using the accelerometer. This android app uses contours algorithms in OpenCV to detect maze that is drawn on the paper. This app uses JBox2d engine for simulating the rigid bodies.



<b>Download APK</b>

Apk can be found in apk folder, download and install the apk.

<b>Modifications if needed</b>
1) <b>Resolution:</b> Resolution is fixed to 1280x960 (less than or equal). Resolution can be changed by modifing this [line](https://github.com/Rohithkvsp/Form-N-Fun/blob/master/app/src/main/java/com/formfun/MainActivity.java#L284).

2) <b>Limiting the Pitch and Roll:</b> Pitch and Roll are limited to 3.8, limiting value can be changed by modifing [MIN](https://github.com/Rohithkvsp/Form-N-Fun/blob/master/app/src/main/java/com/formfun/graphics/GraphicThread.java#L43) and [MAX](https://github.com/Rohithkvsp/Form-N-Fun/blob/master/app/src/main/java/com/formfun/graphics/GraphicThread.java#L44). [Here](https://github.com/Rohithkvsp/Form-N-Fun/blob/master/app/src/main/java/com/formfun/graphics/GraphicThread.java#L69) is the implementation of limiting function.

3) <b>Force on the ball:</b> Force on the ball can be changed [here](https://github.com/Rohithkvsp/Form-N-Fun/blob/master/app/src/main/java/com/formfun/graphics/Ball.java#L69).

4) <b>Color of the ball:</b> Color of the ball can be modified [here](https://github.com/Rohithkvsp/Form-N-Fun/blob/master/app/src/main/java/com/formfun/graphics/Ball.java#L57).


Download <b> Android NDK r16b </b> and extract the file, On Mac I extracted to /Users/Name/Documents/Android/OpenCV-android-sdk-4.1.0

Open the <b>local.properties</b> file and set <b>ndk.dir</b> to the path of Android NDK folder.

on Windows local.properties look like this.

    sdk.dir = C\:\\android_sdk
    ndk.dir = C\:\\android_ndk

on Mac local.properties look like this.

    sdk.dir = /Users/Name/Library/Android/sdk
    ndk.dir = /Users/Name/Documents/Android/android-ndk-r16b

<b>Build Instructions with CV4 </b>

Download <b>OpenCV for Android</b> (I used OpenCV 4.1.0) from http://opencv.org/releases.html and extract the file. I extracted the file to C:\Opencv\OpenCV-android-sdk-4.1.0 (On Windows) or /Users/Name/Documents/Android/OpenCV-android-sdk-4.1.0 (On Mac)


Open the <b>Android.mk</b> file in <b>jni folder</b>, modify the <b>export OPENCV_PACKAGE_DIR</b> to extracted OpenCV Path

on Windows. 
    
    export OPENCV_PACKAGE_DIR = C:/Opencv/OpenCV-android-sdk-4.1.0

on Mac. 

    export OPENCV_PACKAGE_DIR = /Users/Name/Documents/Android/OpenCV-android-sdk-4.1.0
    
<b>Run the app</b> after making the above changes.


<b>Build Instructions with CV3 </b>

Download <b>OpenCV for Android</b> (I used OpenCV 3.4.1) from http://opencv.org/releases.html and extract the file. I extracted the file to C:\Opencv\OpenCV-android-sdk-3.4.1 (On Windows) or /Users/Name/Documents/Android/OpenCV-android-sdk-3.4.1 (On Mac)


Open the <b>Android.mk</b> file in <b>jni folder</b>, modify the <b>export OPENCV_PACKAGE_DIR</b> to extracted OpenCV Path

on Windows. 
    
    export OPENCV_PACKAGE_DIR = C:/Opencv/OpenCV-android-sdk-3.4.1

on Mac. 

    export OPENCV_PACKAGE_DIR = /Users/Name/Documents/Android/OpenCV-android-sdk-3.4.1
    
remove LOCAL_CPPFLAGS in [line 14](https://github.com/Rohithkvsp/Form-N-Fun/blob/43dea5c6e01c70f79c20ed3d5745a934a796658e/app/src/main/jni/Android.mk#L14) in Android.mk to build with CV3

    LOCAL_CPPFLAGS := -DCV4

<b>Run the app</b> after making the above changes.

<b>Note:</b>

I build it on Mac for all the Devices but I tested it on <b>Google PIXEL XL</b>.
    
    APP_ABI := arm64-v8a armeabi-v7a x86 x86_64

If you want to build the App for a <b>particular device</b> then modify <b>APP_ABI to device ABIs</b> in Application.mk in jni folder.

For example, to build App only for <b>Google PIXEL XL</b> then modify <b>APP_ABI</b> to <b>APP_ABI:=arm64-v8a</b> in Application.mk (Pixel XL has ARMv8 based CPU supports AArch64).

    APP_ABI := arm64-v8a

