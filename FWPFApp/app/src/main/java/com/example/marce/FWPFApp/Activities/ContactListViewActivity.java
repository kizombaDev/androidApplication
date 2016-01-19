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
import android.os.Handler;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/*
* this activity show the list of all available contacts
* each row shows a red arrow to the contact, the name of the contact and the distance to the contact
* This class uses the location and the sensor manager
*
* Datei: ContactListViewActivity  Autor: Marcel
* Datum: 17.12   Version: <Versionsnummer>
* Historie:
* 17.12: Marcel creates the activity
*
*/

public class ContactListViewActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private ContactArrayAdapter contactArrayAdapter;
    private boolean isLocationProviderEnabled = true;
    private AngleCalculationHelper angleCalculationHelper;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private SensorManager mSensorManager;
    private LocationManager locationManager;
    private List<Contact> contactsWithLocation;
    private Timer contactUpdateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //the support only the protrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_user_list_view);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //The angle helper should update the angle every 300 ms
        angleCalculationHelper = new AngleCalculationHelper(300);
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
                            triggerGetAllContactsLocationData();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        contactUpdateTimer.schedule(doAsynchronousTask, 0, 100000);
    }

    private void stopContactUpdateTrigger() {
        contactUpdateTimer.cancel();
        contactUpdateTimer.purge();
    }

    private void triggerGetAllContactsLocationData() {
        GetAllContactsLocationDataTask task = new GetAllContactsLocationDataTask(this);
        task.execute((Void) null);
    }


    public class GetAllContactsLocationDataTask extends AsyncTask<Void, Void, Boolean> {
        private final Context context;

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

                    Contact contact = new Contact(id, userName, location);

                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    String string1 = currentJson.getString("LocationUpdateTime");
                    Date locationUpdateTime = df1.parse(string1);
                    contact.setLocationUpdateTime(locationUpdateTime);

                    contactsWithLocationList.add(contact);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            for (Contact c : GenerateSampleContacts()) {
                contactsWithLocationList.add(c);
            }

            final ListView userListView = (ListView) findViewById(R.id.userListView);

            if (contactsWithLocation == null) {
                contactsWithLocation = new ArrayList<>();
                contactsWithLocation.addAll(contactsWithLocationList);
                contactArrayAdapter = new ContactArrayAdapter(context, contactsWithLocation);
                userListView.setAdapter(contactArrayAdapter);
                AddOnItemClickListener(userListView);
            } else {
                contactsWithLocation.clear();
                contactsWithLocation.addAll(contactsWithLocationList);
                ContactArrayAdapter contactArrayAdapter = (ContactArrayAdapter) userListView.getAdapter();
                contactArrayAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Create sample contacts for the listView
     *
     * @return four contacts with sample data
     */
    @NonNull
    private Contact[] GenerateSampleContacts() {
        Contact[] result = new Contact[]{
                new Contact("Max Berlin", SampleLocations.getBerlin()),
                new Contact("Peter London", SampleLocations.getLondon()),
                new Contact("Tobias Nürnberg", SampleLocations.getNuernberg()),
                new Contact("Peter Roma", SampleLocations.getRoma())};

        result[0].setLocationUpdateTime(new Date());
        return result;
    }

    /**
     * this method handles the click on a button
     *
     * @param userListView
     */
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
        stopContactUpdateTrigger();
    }

    @Override
    protected void onResume() {
        registerSensors();
        super.onResume();
        startContactUpdateTrigger();
    }

    /**
     * this method is called by the location manager if the location has changed
     * we have to send the new location the the contactArrayAdapter which updates the view
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        if (contactArrayAdapter != null) {
            contactArrayAdapter.locationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        isLocationProviderEnabled = true;
    }

    /**
     * if the user has disabled the gps the remove the distance text and the navigation arrow from the listview
     *
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
        isLocationProviderEnabled = false;
        if (contactArrayAdapter != null) {

            contactArrayAdapter.locationProviderDisabled();
        }
    }

    /**
     * this method is called by the sensor Manager if the manager has a new angle value
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (contactArrayAdapter != null) {
            angleCalculationHelper.setSensorEvent(event);
            if (angleCalculationHelper.hasDeviceAngles()) {
                float deviceDegree = angleCalculationHelper.getDeviceAngleZ();
                contactArrayAdapter.deviceDegreeChanged(deviceDegree);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * register all sensor Manager
     */
    private void registerSensors() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);

        } catch (SecurityException e) {
            Log.e("GPS", "No permissions", e);
        }

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * unregister all SensorManager
     */
    private void unregisterSensors() {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.e("GPS", "No permissions", e);
        }

        mSensorManager.unregisterListener(this);
    }
}
