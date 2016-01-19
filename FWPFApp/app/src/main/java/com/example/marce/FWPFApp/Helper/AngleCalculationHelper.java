package com.example.marce.FWPFApp.Helper;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;


/*
*
* the android api delivers to float array which contains the angle values
* This helper class calculates the angle values from the float array content
* this functionality is used in two activities
*
* Datei: NavigationActivity  Autor: Marcel
* Datum: 22.12  Version: <Versionsnummer>
* Historie:
* 22.12: Marcel create the class (this functions are taken out from an activity)
*
*/

public class AngleCalculationHelper {
    private float deviceDegree;
    private boolean hasDeviceDegree = false;
    private long time;
    private long sensorUpdateMilliSeconds;
    private float[] mGravity = null;
    private float[] mGeomagnetic = null;
    private float deviceInclinationAngle;

    /**
     * Creates the AngleCalculationHelper
     *
     * @param sensorUpdateMilliSeconds indicates the update interval of the angle value
     */
    public AngleCalculationHelper(long sensorUpdateMilliSeconds) {
        this.sensorUpdateMilliSeconds = sensorUpdateMilliSeconds;
        time = System.currentTimeMillis();
    }

    public void setSensorEvent(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = sensorEvent.values;

        long currentTime = System.currentTimeMillis();
        if (mGravity != null && mGeomagnetic != null && currentTime - time > sensorUpdateMilliSeconds) {
            time = currentTime;
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                hasDeviceDegree = true;
                this.deviceDegree = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
                this.deviceInclinationAngle = (float) (Math.toDegrees(orientation[1]) + 360) % 360;
            }
        }
    }

    /**
     * Returns true if the Helper has angle values, otherwise false
     *
     * @return true if the helper has angle values, otherwise false
     */
    public boolean hasDeviceAngles() {
        return hasDeviceDegree;
    }

    /***
     * @return Returns the Z-angle
     */
    public float getDeviceAngleZ() {
        return deviceDegree;
    }

    /**
     * @return Return the X-angle
     */
    public float getDeviceAngleX() {
        return deviceInclinationAngle;
    }

}
