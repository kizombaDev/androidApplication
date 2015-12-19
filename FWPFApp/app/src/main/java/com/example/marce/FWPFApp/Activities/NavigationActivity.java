package com.example.marce.FWPFApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;

import com.example.marce.FWPFApp.DataObjects.Contact;
import com.example.marce.FWPFApp.Helper.CameraView;
import com.example.marce.FWPFApp.Helper.DegreeCalculationHelper;
import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.FWPFApp.OpenGL.NavigationArrowRenderer;

public class NavigationActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private GLSurfaceView glView;
    private NavigationArrowRenderer navigationArrowRenderer;
    private Location currentDeviceLocation;
    private LocationManager locationManager;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float currentDeviceDegree;
    private DegreeCalculationHelper degreeCalculationHelper;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //LinearLayout cameraViewContainer = (LinearLayout)findViewById(R.id.cameraView);

        Intent intent = this.getIntent();
        contact = intent.getParcelableExtra(Globals.navigationActitivyIntend());

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        this.degreeCalculationHelper = new DegreeCalculationHelper(300);

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
        navigationArrowRenderer = new NavigationArrowRenderer();
        glView.setRenderer(navigationArrowRenderer);
        setContentView(glView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterSensors();
        glView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerSensors();
        glView.onResume();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentDeviceLocation = location;
        updateGLArrow();

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

        degreeCalculationHelper.setSensorEvent(event);
        if (degreeCalculationHelper.hasDeviceDegree()) {
            this.currentDeviceDegree = degreeCalculationHelper.getDeviceDegree();
            updateGLArrow();

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateGLArrow() {
        if (currentDeviceLocation == null) {
            /*currentDeviceLocation = new Location("");
            currentDeviceLocation.setLongitude(11.343355);
            currentDeviceLocation.setLatitude(49.565346);*/
            return;
        }

        if (!degreeCalculationHelper.hasDeviceDegree())
            return;

        Location destination = contact.getLocation();
        float nextArrowDegree;
        float degreeToDestination = currentDeviceLocation.bearingTo(destination);

        nextArrowDegree = (degreeToDestination - currentDeviceDegree);
        if (nextArrowDegree < 0) {
            nextArrowDegree += 360;
        }

        navigationArrowRenderer.updateArrowDegree(nextArrowDegree);

    }

    private void registerSensors() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);

        } catch (SecurityException e) {
            Log.e("GPS", "No permissions", e);
        }

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void unregisterSensors() {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.e("GPS", "No permissions", e);
        }

        mSensorManager.unregisterListener(this);
    }
}
