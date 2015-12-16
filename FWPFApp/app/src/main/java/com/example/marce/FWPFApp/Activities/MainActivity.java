package com.example.marce.FWPFApp.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.example.marce.FWPFApp.Helper.Globals;
import com.example.marce.listviewsample.R;
import com.example.marce.FWPFApp.Service.LocationService;

public class MainActivity extends AppCompatActivity {

    private static int registerActivityRequestCode = 11;
    private static int contactListViewActivityRequestCode = 22;
    private static int locationSourceActivityRequestCode = 33;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent locationServiceIntent = new Intent(this, LocationService.class);
        startService(locationServiceIntent);

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        if(isGPSEnabled()) {
            startActivities();
        }
        else
        {
            askUserToEnableGPS();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == registerActivityRequestCode) {
            if (resultCode == RESULT_OK) {
                startListViewActivity();
            } else {
                finish();
            }
        }
        else if(requestCode == contactListViewActivityRequestCode)
        {
            finish();
        }
        else if (requestCode == locationSourceActivityRequestCode)
        {
            startActivities();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        locationManager = null;
    }

    private void startActivities() {
        if (isUserRegistered()) {
            startListViewActivity();
        } else {
            startRegistrationActivity();
        }
    }

    private void startRegistrationActivity() {
        Intent registrationIntent = new Intent(MainActivity.this, RegistrationActivity.class);
        MainActivity.this.startActivityForResult(registrationIntent, registerActivityRequestCode);
    }

    private boolean isUserRegistered() {
        SharedPreferences settings = getSharedPreferences(Globals.settingFile(), MODE_PRIVATE);
        if(settings.contains(Globals.setttingIsUserRegistered()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void startListViewActivity() {
        Intent contactListViewIntent = new Intent(MainActivity.this, ContactListViewActivity.class);
        MainActivity.this.startActivityForResult(contactListViewIntent, contactListViewActivityRequestCode);
    }

    public boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void askUserToEnableGPS() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        alertDialog.setTitle(R.string.GPSDialogTitle);

        alertDialog.setMessage(R.string.GPSDialogMessage);

        alertDialog.setPositiveButton(R.string.GPSDialogSettings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivityForResult(intent, locationSourceActivityRequestCode);
            }
        });

        alertDialog.setNegativeButton(R.string.GPSDialogCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivities();
            }
        });

        alertDialog.show();
    }
}
