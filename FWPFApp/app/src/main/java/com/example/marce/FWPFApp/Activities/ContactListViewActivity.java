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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.marce.FWPFApp.DataObjects.Contact;
import com.example.marce.FWPFApp.Helper.AngleCalculationHelper;
import com.example.marce.FWPFApp.Helper.ContactArrayAdapter;
import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.FWPFApp.Helper.PhonebookRetriever;
import com.example.marce.FWPFApp.R;
import com.example.marce.FWPFApp.SampleLocations;
import com.example.marce.FWPFApp.ServerCommunication.Requests.GetAllContactsLocationDataPostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ContactListViewActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private ContactArrayAdapter contactArrayAdapter;
    private boolean isLocationProviderEnabled = true;
    private AngleCalculationHelper angleCalculationHelper;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private SensorManager mSensorManager;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_user_list_view);


        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        angleCalculationHelper = new AngleCalculationHelper(300);

        initListView();
    }

    private void initListView() {

        //Todo do periodically to get the location updates of the other contacts
        GetAllContactsLocationDataTask task = new GetAllContactsLocationDataTask(this);
        task.execute((Void) null);
    }


    public class GetAllContactsLocationDataTask extends AsyncTask<Void, Void, Boolean> {
        private final Context context;

        private Contact[] contactsWithLocation;
        private JSONArray contactsFromRequestJsonArray;

        public GetAllContactsLocationDataTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            PhonebookRetriever phonebookRetriever = new PhonebookRetriever(context);
            String[] allPhonebookNumbers = phonebookRetriever.getAllPhonebookNumbers();

            GetAllContactsLocationDataPostRequest request = new GetAllContactsLocationDataPostRequest(allPhonebookNumbers);
            contactsFromRequestJsonArray = request.execute();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            List<Contact> contactsWithLocationList = new ArrayList<Contact>();
            JSONObject currentJson;
            for (int i = 0; i < contactsFromRequestJsonArray.length(); i++) {
                try {
                    currentJson = contactsFromRequestJsonArray.getJSONObject(i);
                    String userName = currentJson.getString("Username");
                    double latitude = Double.parseDouble(currentJson.getString("Latitude"));
                    double longitude = Double.parseDouble(currentJson.getString("Longitude"));
                    String id = currentJson.getString("Id");
                    Location location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    contactsWithLocationList.add(new Contact(userName, location));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (Contact c : GenerateSampleContacts()) {
                contactsWithLocationList.add(c);
            }

            contactsWithLocation = new Contact[contactsWithLocationList.size()];
            contactsWithLocation = contactsWithLocationList.toArray(contactsWithLocation);

            contactArrayAdapter = new ContactArrayAdapter(context, contactsWithLocation);
            final ListView userListView = (ListView) findViewById(R.id.userListView);
            userListView.setAdapter(contactArrayAdapter);
            AddOnItemClickListener(userListView);

        }
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
                if (isLocationProviderEnabled) {
                    Contact contactToNaviagte = contactArrayAdapter.getUserOfPosition(position);
                    if (contactToNaviagte != null) {
                        Intent myIntent = new Intent(ContactListViewActivity.this, NavigationActivity.class);
                        myIntent.putExtra(Globals.navigationActitivyIntend(), contactToNaviagte);
                        ContactListViewActivity.this.startActivity(myIntent);
                    }
                } else {
                    Toast.makeText(ContactListViewActivity.this, R.string.messageEnableGPS, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onPause() {
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        //sauber????
        // contactarrayadapter unter uumständen noch nicht initialisiert zu dem zeitpunkt, weil das erst in onpostexecute vom asynctask passiert
        if(contactArrayAdapter != null){
            angleCalculationHelper.setSensorEvent(event);
            if (angleCalculationHelper.hasDeviceDegree()) {
                float deviceDegree = angleCalculationHelper.getDeviceDegree();
                contactArrayAdapter.deviceDegreeChanged(deviceDegree);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
