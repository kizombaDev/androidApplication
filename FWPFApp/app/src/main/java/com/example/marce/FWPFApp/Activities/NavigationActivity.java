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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;

import com.example.marce.FWPFApp.DataObjects.Contact;
import com.example.marce.FWPFApp.Helper.AngleCalculationHelper;
import com.example.marce.FWPFApp.Helper.CameraSurfaceView;
import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.FWPFApp.OpenGL.NavigationArrowRenderer;
import com.example.marce.FWPFApp.OpenGL.NavigationTextureRenderer;
import com.example.marce.FWPFApp.ServerCommunication.Requests.GetContactLocationDataPostRequest;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/*
*
* this class handle the big blue navigation arrow
*
* Datei: NavigationActivity  Autor: Marcel
* Datum: 17.12  Version: <Versionsnummer>
* Historie:
* 17.12: Marcel creates the class
*/

public class NavigationActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private GLSurfaceView glArrowSurfaceView;
    private NavigationArrowRenderer navigationArrowRenderer;
    private NavigationTextureRenderer navigationTextureRenderer;
    private Location currentDeviceLocation;
    private LocationManager locationManager;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float deviceAngleZ;
    private AngleCalculationHelper angleCalculationHelper;
    private Contact contact;
    private Timer contactUpdateTimer;
    private GLSurfaceView glTextureSurfaceView;
    private float deviceAngleX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //get the contact from the conactListViewActivity
        Intent intent = this.getIntent();
        contact = intent.getParcelableExtra(Globals.navigationActitivyIntend());


        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        try {
            this.currentDeviceLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            Log.e("GPS", "No permissions", e);
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        this.angleCalculationHelper = new AngleCalculationHelper(300);

        initGLView();
        initCameraView();
    }

    private void startContactUpdateTrigger() {
        final Handler handler = new Handler();
        contactUpdateTimer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            triggerGetContactLocationData();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        contactUpdateTimer.schedule(doAsynchronousTask, 0, 5000);
    }

    private void stopContactUpdateTrigger() {
        contactUpdateTimer.cancel();
        contactUpdateTimer.purge();
    }

    private void triggerGetContactLocationData() {
        GetContactLocationDataTask task = new GetContactLocationDataTask();
        task.execute((Void) null);
    }

    /**
     * init the camera view and add this surface to the contentView
     */
    private void initCameraView() {
        if (!Globals.isEmulator()) {
            CameraSurfaceView cameraSurfaceView = new CameraSurfaceView(this);
            addContentView(cameraSurfaceView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * creates and set the two GLSurfaceViews with the arrow and the text info
     */
    private void initGLView() {
        //creates the arrow surfaceView
        glArrowSurfaceView = new GLSurfaceView(this);
        glArrowSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glArrowSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        navigationArrowRenderer = new NavigationArrowRenderer();
        glArrowSurfaceView.setRenderer(navigationArrowRenderer);
        addContentView(glArrowSurfaceView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        //creates the texture surfaceView
        glTextureSurfaceView = new GLSurfaceView(this);
        glTextureSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glTextureSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        navigationTextureRenderer = new NavigationTextureRenderer(this);
        navigationTextureRenderer.setContactName(contact.getName());
        glTextureSurfaceView.setRenderer(navigationTextureRenderer);
        addContentView(glTextureSurfaceView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister and stop the surfaceViews if the app are paused
        unregisterSensors();
        glArrowSurfaceView.onPause();
        glTextureSurfaceView.onPause();
        stopContactUpdateTrigger();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //register and start the surfaceViews if the app/activity is resumed
        registerSensors();
        glArrowSurfaceView.onResume();
        glTextureSurfaceView.onResume();
        startContactUpdateTrigger();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentDeviceLocation = location;
        updateGLArrow();
        updateGLContactInformation();
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


    /**
     * the have to update the navigation arrow if one angle has changed
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        angleCalculationHelper.setSensorEvent(event);
        if (angleCalculationHelper.hasDeviceAngles()) {
            this.deviceAngleZ = angleCalculationHelper.getDeviceAngleZ();
            this.deviceAngleX = angleCalculationHelper.getDeviceAngleX();
            updateGLArrow();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * handles the GLArrow update
     * calculates the angle to the targetLocation
     */
    private void updateGLArrow() {
        if (currentDeviceLocation == null) {
            return;
        }

        if (!angleCalculationHelper.hasDeviceAngles())
            return;

        Location destination = contact.getLocation();
        //calculates the angle between to locations
        float angleToDestination = currentDeviceLocation.bearingTo(destination);

        //calculates the angle of the navigation arrow
        float angleToTargetLocationZ = (angleToDestination - deviceAngleZ);
        if (angleToTargetLocationZ < 0) {
            angleToTargetLocationZ += 360;
        }

        navigationArrowRenderer.updateAngleToTargetLoactionZ(angleToTargetLocationZ);
        navigationArrowRenderer.updateDeviceAngleX(deviceAngleX);
    }

    private void updateGLContactInformation() {
        if (currentDeviceLocation == null) {
            return;
        }

        navigationTextureRenderer.setDistanceInMeters(currentDeviceLocation.distanceTo(contact.getLocation()));

        Date locationUpdateTime = contact.getLocationUpdateTime();
        navigationTextureRenderer.setLastUpdate(locationUpdateTime);
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


    public class GetContactLocationDataTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            GetContactLocationDataPostRequest request = new GetContactLocationDataPostRequest(contact.getId());
            request.execute(contact);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            updateGLArrow();
            updateGLContactInformation();
        }
    }
}
