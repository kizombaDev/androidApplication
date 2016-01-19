package com.example.marce.FWPFApp.Helper;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;


/**
 * this class creats a surfaceView, which are rendered in to the navigationactivity
 * the cameraSurfaceView use the android.hardware.camera, to render the camera preview
 * this camera class is deprected from anrdoid 5.x
 * in the lecture we are develop apps for android 4.x so we used this class anyway
 * Datei: CameraSurfaceView  Autor: Marcel
 * Datum: 22.12  Version: <Versionsnummer>
 * Historie:
 * 19.12: Marcel add the cameraview logic
 * 16.12: Marcel creats the class
 */
public class CameraSurfaceView extends SurfaceView implements Callback {
    private Camera camera;

    public CameraSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // Once the surface is created, simply open a handle to the camera hardware.
        camera = Camera.open();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // This method is called when the surface changes, e.g. when it's size is set.
        // We use the opportunity to initialize the camera preview display dimensions.
        Camera.Parameters p = camera.getParameters();
        camera.setDisplayOrientation(90);
        camera.setParameters(p);

        // We also assign the preview display to this surface...
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ...and start previewing. From now on, the camera keeps pushing preview
        camera.startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Once the surface gets destroyed, we stop the preview mode and release
        // the whole camera since we no longer need it.
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}