package com.example.marce.FWPFApp.Helper;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class DegreeCalculationHelper {
    private float deviceDegree;
    private boolean hasDeviceDegree = false;
    private long time;
    private long sensorUpdateMilliSeconds;
    float[] mGravity = null;
    float[] mGeomagnetic = null;

    public DegreeCalculationHelper(long sensorUpdateMilliSeconds) {
        this.sensorUpdateMilliSeconds = sensorUpdateMilliSeconds;
        time = System.currentTimeMillis();
    }

    public void setSensorEvent(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = sensorEvent.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuthInRadians = orientation[0];
                float currentDegree = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

                long currentTime = System.currentTimeMillis();
                if (currentTime - time > sensorUpdateMilliSeconds) {
                    time = currentTime;
                    this.deviceDegree = currentDegree;
                    hasDeviceDegree = true;
                }

            }
        }
    }

    public boolean hasDeviceDegree() {
        return hasDeviceDegree;
    }

    public float getDeviceDegree() {
        return deviceDegree;
    }
}
