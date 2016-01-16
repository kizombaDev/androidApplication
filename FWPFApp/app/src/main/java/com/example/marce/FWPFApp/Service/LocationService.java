package com.example.marce.FWPFApp.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.FWPFApp.ServerCommunication.Requests.UpdateMyCurrentLocationPutRequest;

/**
 * Created by Marcel Swoboda
 * Der LocationService schickt die aktuellen Standortdaten an den Server
 * Sobald neue Standortdaten vorliegen wir die Methoe onLocationChanged vom LocationManager aufgerufen.
 * Der Server kann den Client anhand einer ID, welcher der Client bei der Anmeldung erhalten hat und
 * sich in den  SharedPreferences speichert, zuordnen.
 */

public class LocationService extends Service  implements LocationListener {
    private LocationManager locationManager;
    private UpdateMyLocationTask updateMyLocationTask;


    public LocationService() {

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
            //Meldet en LocationService für neue Standortdaten an ... alle 15 Sekunden oder bei 20 Meter
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
        //Neue Daten an den Server schicken
        updateMyLocationTask = new UpdateMyLocationTask(location);
        updateMyLocationTask.execute((Void) null);
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

    public class UpdateMyLocationTask extends AsyncTask<Void, Void, Boolean>{

        private final Location location;

        public UpdateMyLocationTask(Location location){
            this.location = location;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Auslesen der Id aus den SharedPreferences
            SharedPreferences settings = getSharedPreferences(Globals.settingFile(), MODE_PRIVATE);
            String myId = settings.getString(Globals.settingUserId(), "-1");
            UpdateMyCurrentLocationPutRequest request = new UpdateMyCurrentLocationPutRequest(myId, location);
            request.execute();
            return true;
        }
    }
}
