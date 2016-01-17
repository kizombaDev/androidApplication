package com.example.marce.FWPFApp.Helper;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/*
* Datei: NavigationActivity  Autor: Marcel
* Datum: 22.12  Version: <Versionsnummer>
* Historie:
* 22.12: Marcel Klasse erst (Funktinalität aus der Activity herausgezogen
*
* Von den Sensoren der Android API bekommt man zwei float-Arrays
* Diese müssen dann noch in richtige Winkelangaben umgerechnet werden
* Da diese Funktionalität in zwei Activities benötigt wird, wurde sie ausgelagert in eine Helper Klasse
*
* Dass sich die Pfeile nicht zu schnell drehen, wird die Winkelangabe nur alle x sekunden aktualisiert
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
     * Erstelt die Helper Klasse
     *
     * @param sensorUpdateMilliSeconds Gibt an nach wie vielen ms die Winkelangaben aktualisiert werden sollen
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

    public boolean hasDeviceAngles() {
        return hasDeviceDegree;
    }

    public float getDeviceAngleZ() {
        return deviceDegree;
    }

    public float getDeviceAngleX() {
        return deviceInclinationAngle;
    }

}
