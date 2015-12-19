package com.example.marce.FWPFApp.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.marce.FWPFApp.ServerCommunication.ServerCommunicationManager;

public class LocationService extends Service  implements LocationListener {
    private LocationManager locationManager;

    ServerCommunicationManager serverCommunicationManager;

    public LocationService() {

    }

    public void setServerCommunicationManager(ServerCommunicationManager serverCommunicationManager) {
        this.serverCommunicationManager = serverCommunicationManager;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 20, this);
        } catch (SecurityException e) {
            Log.e("GPS", "No permissions", e);
        }

        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.e("GPS", "No permissions", e);
        }

        locationManager = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Servie: update the Location", Toast.LENGTH_SHORT).show();

        //Patrick
        if(serverCommunicationManager != null)
            serverCommunicationManager.sendCurrentLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
