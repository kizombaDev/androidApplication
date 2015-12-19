package com.example.marce.FWPFApp.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;

import com.example.marce.FWPFApp.DataObjects.Contact;
import com.example.marce.FWPFApp.Helper.CameraView;
import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.FWPFApp.OpenGL.NavigationArrowRenderer;
import com.example.marce.FWPFApp.R;

public class NavigationActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private GLSurfaceView glView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //LinearLayout cameraViewContainer = (LinearLayout)findViewById(R.id.cameraView);

        Intent intent = this.getIntent();
        Contact contact = intent.getParcelableExtra(Globals.navigationActitivyIntend());

        initGLView();
        initCameraView();
    }

    private void initCameraView() {
        if(!Globals.isEmulator()) {
            CameraView cameraView = new CameraView(this);
            addContentView(cameraView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void initGLView() {
        // Now let's create an OpenGL surface.
        glView = new GLSurfaceView( this );
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glView.setRenderer(new NavigationArrowRenderer());
        setContentView(glView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        glView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        glView.onResume();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
