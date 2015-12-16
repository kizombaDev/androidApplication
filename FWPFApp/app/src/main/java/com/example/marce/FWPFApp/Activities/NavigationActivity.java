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
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;

import com.example.marce.FWPFApp.DataObjects.Contact;
import com.example.marce.FWPFApp.Helper.CameraView;
import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.FWPFApp.OpenGL.NavigationArrowRenderer;
import com.example.marce.listviewsample.R;

public class NavigationActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private GLSurfaceView glView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //setContentView(R.layout.activity_navigation);
        LinearLayout cameraViewContainer = (LinearLayout)findViewById(R.id.cameraView);

        Intent intent = this.getIntent();
        Contact contact = intent.getParcelableExtra(Globals.navigationActitivyIntend());

        // Now let's create an OpenGL surface.
        glView = new GLSurfaceView( this );
        // To see the camera preview, the OpenGL surface has to be created translucently.
        // See link above.
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // The renderer will be implemented in a separate class, GLView, which I'll show next.
        glView.setRenderer(new NavigationArrowRenderer());
        // Now set this as the main view.
        //setContentView( glView  );
        //cameraViewContainer.addView(glView);

        setContentView(glView);

        if(!Globals.isEmulator()) {
            CameraView cameraView = new CameraView(this);
            addContentView(cameraView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
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
