package com.example.marce.FWPFApp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.listviewsample.R;
import com.example.marce.FWPFApp.SampleLocations;
import com.example.marce.FWPFApp.DataObjects.Contact;
import com.example.marce.FWPFApp.Helper.ContactArrayAdapter;


public class ContactListViewActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private ContactArrayAdapter contactArrayAdapter;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private SensorManager mSensorManager;
    private LocationManager locationManager;
    private boolean isLocationProviderEnabled = true;
    private long sensorMilliSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorMilliSeconds = System.currentTimeMillis();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_user_list_view);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        initListView();
    }

    private void initListView()
    {
        Contact[] contacts = GenerateSampleContacts();
        contactArrayAdapter = new ContactArrayAdapter(this, contacts);

        final ListView userListView = (ListView) findViewById(R.id.userListView);
        userListView.setAdapter(contactArrayAdapter);
        AddOnItemClickListener(userListView);
    }

    @NonNull
    private Contact[] GenerateSampleContacts() {
        return new Contact[]{
                new Contact("Max Berlin", SampleLocations.getBerlin()),
                new Contact("Peter London", SampleLocations.getLondon()),
                new Contact("Tobias Nürnberg", SampleLocations.getNuernberg()),
                new Contact("Peter Roma", SampleLocations.getRoma())};
    }

    private void AddOnItemClickListener(ListView userListView) {
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isLocationProviderEnabled) {
                    Contact contactToNaviagte = contactArrayAdapter.getUserOfPosition(position);
                    if (contactToNaviagte != null) {
                        Intent myIntent = new Intent(ContactListViewActivity.this, NavigationActivity.class);
                        myIntent.putExtra(Globals.navigationActitivyIntend(), contactToNaviagte);
                        ContactListViewActivity.this.startActivity(myIntent);
                    }
                }
                else
                {
                    Toast.makeText(ContactListViewActivity.this, R.string.messageEnableGPS, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onPause()
    {
        unregisterSensors();
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerSensors();
        super.onResume();
    }

    @Override
    public void onLocationChanged(Location location) {
        contactArrayAdapter.locationChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        isLocationProviderEnabled = true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        isLocationProviderEnabled = false;
        contactArrayAdapter.locationProviderDisabled();
    }

    float[] mGravity = null;
    float[] mGeomagnetic = null;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
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
                if(currentTime - sensorMilliSeconds > 300)
                {
                    sensorMilliSeconds = currentTime;
                    contactArrayAdapter.deviceDegreeChanged(currentDegree);
                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensors()
    {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 5, this);

        } catch (SecurityException e) {
            Log.e("GPS", "No permissions", e);
        }

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    private void unregisterSensors()
    {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.e("GPS", "No permissions", e);
        }

        mSensorManager.unregisterListener(this);
    }
}
