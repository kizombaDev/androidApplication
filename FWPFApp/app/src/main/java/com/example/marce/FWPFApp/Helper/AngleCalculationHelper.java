package com.example.marce.FWPFApp.Helper;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

public class AngleCalculationHelper {
    private float deviceDegree;
    private boolean hasDeviceDegree = false;
    private long time;
    private long sensorUpdateMilliSeconds;
    float[] mGravity = null;
    float[] mGeomagnetic = null;
    private float deviceInclinationAngle;

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
                Log.i("Marcel", "inclianation: " + deviceInclinationAngle);

            }
        }
    }

    public boolean hasDeviceDegree() {
        return hasDeviceDegree;
    }

    public float getDeviceDegree() {
        return deviceDegree;
    }

    public float getDeviceInclinationAngle() {
        return deviceInclinationAngle;
    }
}