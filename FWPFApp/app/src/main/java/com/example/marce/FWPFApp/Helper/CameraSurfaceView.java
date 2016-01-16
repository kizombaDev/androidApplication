package com.example.marce.FWPFApp.Helper;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;


/**
 * Diese Klasse stellt eine SurfaceView zur Verfügung, welche innerhalb der View gerendert wird.
 * Diese Klasse CameraView nutzt intern die Klasse android.hardware.Camera, welche ab der Android Version 5.0 als veraltet gilt.
 * Da wir in der Vorlesung jedoch gezielt für Android 4.x entwickeln sollten nutzen wir diese Klasse trotzdem.
 * <p/>
 * Created by Marcel Swoboda
 */
public class CameraSurfaceView extends SurfaceView implements Callback {
    private Camera camera;

    public CameraSurfaceView(Context context) {
        super(context);
        // We're implementing the Callback interface and want to get notified
        // about certain surface events.
        getHolder().addCallback(this);
        // We're changing the surface to a PUSH surface, meaning we're receiving
        // all buffer data from another component - the camera, in this case.
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
        // images to the surface.
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