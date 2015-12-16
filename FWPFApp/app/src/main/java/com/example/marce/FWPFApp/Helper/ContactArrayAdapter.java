package com.example.marce.FWPFApp.Helper;


import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marce.FWPFApp.DataObjects.Contact;
import com.example.marce.listviewsample.R;


public class ContactArrayAdapter extends ArrayAdapter<Contact> {


    private final Context context;
    private final RowObject[] rowObjects;
    private Location currentLocation;

    public ContactArrayAdapter(Context context, Contact[] contacts) {
        super(context, -1, contacts);
        this.context = context;
        this.rowObjects = new RowObject[contacts.length];

        initRowObjects(contacts);
    }

    private void initRowObjects(Contact[] contacts) {
        for (int i = 0; i < rowObjects.length; i++) {
            rowObjects[i] = new RowObject();
            rowObjects[i].setContact(contacts[i]);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.ContactNameLabel);
        TextView distanceView = (TextView) rowView.findViewById(R.id.distance);
        rowObjects[position].setDistanceTextView(distanceView);
        distanceView.setText(R.string.NoGPSSingal);
        textView.setText(rowObjects[position].getContact().getName().toString());

        ImageView iconBlackView = (ImageView) rowView.findViewById(R.id.iconBlack);
        ImageView iconView = (ImageView) rowView.findViewById(R.id.icon);
        iconView.setVisibility(View.INVISIBLE);
        rowObjects[position].setIcon(iconView);
        rowObjects[position].setIconBlack(iconBlackView);
        return rowView;
    }

    private void updateDistance(Location location) {

        for (int i = 0; i < rowObjects.length; i++) {
            String distanceText = null;
            if (location == null) {
                distanceText = "";
            } else {
                float distance = rowObjects[i].getContact().distanceToLocation(location);
                distance = distance / 1000;
                distanceText = String.format("%.1f", distance) + " km";
            }
            rowObjects[i].getDistanceTextView().setVisibility(View.VISIBLE);
            rowObjects[i].getDistanceTextView().setText(distanceText);
        }
    }

    public void locationChanged(Location location) {
        updateDistance(location);
        this.currentLocation = location;
    }

    public void deviceDegreeChanged(float currentDegree) {
        updateDeviceDegree(currentLocation, currentDegree);
    }

    private void updateDeviceDegree(Location currentLocation, float currentDegree) {

        if (currentLocation == null)
            return;

        for (int i = 0; i < rowObjects.length; i++) {

            Location destination = rowObjects[i].getContact().getLocation();

            float currentCompassDegree;

            float degreeToDestination = currentLocation.bearingTo(destination);

            if (currentDegree < degreeToDestination) {
                currentCompassDegree = 180 + (degreeToDestination - currentDegree);
            } else {
                currentCompassDegree = 180 + (degreeToDestination - currentDegree);
            }

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    rowObjects[i].getLastIconDegree(),
                    currentCompassDegree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            // how long the animation will take place
            ra.setDuration(210);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            RowObject currentRowObject = rowObjects[i];
            currentRowObject.getIcon().setVisibility(View.VISIBLE);
            currentRowObject.getIconBlack().setVisibility(View.INVISIBLE);
            currentRowObject.getIcon().startAnimation(ra);
            currentRowObject.setLastIconDegree(currentCompassDegree);

        }
    }

    public Contact getUserOfPosition(int position) {

        if (rowObjects != null && position < rowObjects.length)
            return rowObjects[position].getContact();
        else
            return null;
    }

    public void locationProviderDisabled() {
        currentLocation = null;

        if (rowObjects == null)
            return;

        for (int i = 0; i < rowObjects.length; i++) {

            RowObject currentRowObject = rowObjects[i];

            if (currentRowObject != null) {

                if (currentRowObject.getDistanceTextView() != null)
                    currentRowObject.getDistanceTextView().setText(R.string.NoGPSSingal);
                if (currentRowObject.getIcon() != null) {
                    currentRowObject.getIcon().clearAnimation();
                    currentRowObject.getIcon().setVisibility(View.INVISIBLE);
                }
                if (currentRowObject.getIconBlack() != null)
                    currentRowObject.getIconBlack().setVisibility(View.VISIBLE);
            }
        }
    }
}

