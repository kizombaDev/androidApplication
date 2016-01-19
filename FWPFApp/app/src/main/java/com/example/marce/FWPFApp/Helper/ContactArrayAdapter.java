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
import com.example.marce.FWPFApp.R;

import java.util.List;

/**
 * Datei: ContactArrayAdapter  Autor: Marcel
 * Datum: 22.12  Version: <Versionsnummer>
 * Historie:
 * 18.12: Marcel creates the class
 */
public class ContactArrayAdapter extends ArrayAdapter<Contact> {
    private final Context context;
    private final RowObject[] rowObjects;
    private Location currentLocation;

    public ContactArrayAdapter(Context context, List<Contact> contacts) {
        super(context, -1, contacts);
        this.context = context;
        this.rowObjects = new RowObject[contacts.size()];

        initRowObjects(contacts);
    }

    /**
     * creates for any contact a rowobject and save the contact into the rowObject
     *
     * @param contacts the list of contacts
     */
    private void initRowObjects(List<Contact> contacts) {
        for (int i = 0; i < rowObjects.length; i++) {
            rowObjects[i] = new RowObject();
            rowObjects[i].setContact(contacts.get(i));
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //set the row-lqyout of the listView; this layout definies the position of the icon (red arrow) and the text controls
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.ContactNameLabel);
        TextView distanceView = (TextView) rowView.findViewById(R.id.distance);
        rowObjects[position].setDistanceTextView(distanceView);
        if (currentLocation == null) {
            distanceView.setText("");
        } else {
            updateTheDistanceText(currentLocation, position);
        }
        textView.setText(rowObjects[position].getContact().getName());

        ImageView iconBlackView = (ImageView) rowView.findViewById(R.id.iconBlack);
        ImageView iconView = (ImageView) rowView.findViewById(R.id.icon);
        iconView.setVisibility(View.INVISIBLE);
        rowObjects[position].setIcon(iconView);
        rowObjects[position].setIconBlack(iconBlackView);
        return rowView;
    }

    /**
     * a new location is available and the distance to the other contac has du recalculate
     *
     * @param location the new loaction
     * @param position the index of the row in the listview
     */
    private void updateTheDistanceText(Location location, int position) {
        String distanceText;
        if (location == null) {
            distanceText = "";
        } else {
            float distance = rowObjects[position].getContact().distanceToLocation(location);
            distance = distance / 1000;
            distanceText = String.format("%.1f", distance) + " km";
        }
        rowObjects[position].getDistanceTextView().setVisibility(View.VISIBLE);
        rowObjects[position].getDistanceTextView().setText(distanceText);
    }

    /**
     * set a new location value
     * all distance info have to recalculate
     *
     * @param location the new location
     */
    public void locationChanged(Location location) {
        this.currentLocation = location;
        for (int i = 0; i < rowObjects.length; i++) {
            updateTheDistanceText(location, i);
        }
    }

    /**
     * a new angle is available
     * all arrow have to rotate into the new direction
     *
     * @param currentDegree the new angle
     */
    public void deviceDegreeChanged(float currentDegree) {
        updateDeviceDegree(currentLocation, currentDegree);
    }

    private void updateDeviceDegree(Location currentLocation, float currentDegree) {

        if (currentLocation == null) {
            return;
        }

        for (int i = 0; i < rowObjects.length; i++) {

            Location destination = rowObjects[i].getContact().getLocation();
            float lastIconAngle = rowObjects[i].getLastIconAngle();
            float currentCompassDegree;
            float degreeToDestination = currentLocation.bearingTo(destination);

            currentCompassDegree = 180 + (degreeToDestination - currentDegree);
            if (currentCompassDegree < 0) {
                currentCompassDegree += 360;
            }

            float nextIconAngle = currentCompassDegree;

            if (0 < lastIconAngle && lastIconAngle < 90 && 270 < nextIconAngle && nextIconAngle < 360) {
                nextIconAngle -= 360;
            }

            if (0 < nextIconAngle && nextIconAngle < 90 && 270 < lastIconAngle && lastIconAngle < 360) {
                lastIconAngle -= 360;
            }

            //ceates the animation and set the angles
            //the animation rotate the arrow from the last angle to the next angle
            RotateAnimation ra = new RotateAnimation(
                    lastIconAngle,
                    nextIconAngle,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);


            //the animation should take 210 ms
            ra.setDuration(210);
            ra.setFillAfter(true);

            RowObject currentRowObject = rowObjects[i];
            currentRowObject.getIcon().setVisibility(View.VISIBLE);
            currentRowObject.getIconBlack().setVisibility(View.INVISIBLE);
            currentRowObject.setLastIconAngle(currentCompassDegree);

            //start the animation
            currentRowObject.getIcon().startAnimation(ra);
        }
    }

    public Contact getUserOfPosition(int position) {

        if (rowObjects != null && position < rowObjects.length)
            return rowObjects[position].getContact();
        else
            return null;
    }

    /**
     * if the gps sensor is deactivated the listview should not show infos (arrow and distance text)
     */
    public void locationProviderDisabled() {
        currentLocation = null;

        if (rowObjects == null)
            return;

        for (int i = 0; i < rowObjects.length; i++) {

            RowObject currentRowObject = rowObjects[i];

            if (currentRowObject != null) {

                if (currentRowObject.getDistanceTextView() != null)
                    currentRowObject.getDistanceTextView().setText("");
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

