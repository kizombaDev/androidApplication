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
 * Created by Marcel Swoboda
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
     * Erstellt für jeden Kontakt ein RowObject und speichert die Kontaktdaten dadrin
     *
     * @param contacts Liste aller verfügbaren Kontakte
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

        //Setzt das Row-Layout der ListView; in dem Layout ist definiert wo das Icon (Pfeil) oder die Texte platziert sind
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
     * Es gibt neue eigene Standortdaten und die Entfernung zu den anderen Kontakten muss neu berechnet werden
     *
     * @param location neuer eigener Standort
     * @param position Index der Zeile in der ListView für die gerade die Methode aufgerufen wird
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
     * setzt eine neue eigene Location
     * Daraufhin müssen die Entfernungsangaben neu berechnet werden
     * @param location Neuer Standort
     */
    public void locationChanged(Location location) {
        this.currentLocation = location;
        for (int i = 0; i < rowObjects.length; i++) {
            updateTheDistanceText(location, i);
        }
    }

    /**
     * Es gibt eine neue Winkel vom Sensor
     * Daraufhin müssen die roten Pfeile gedreht werden
     * @param currentDegree Neue Winkelangabe
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
            if(currentCompassDegree < 0)
            {
                currentCompassDegree += 360;
            }

            float nextIconAngle = currentCompassDegree;

            if (0 < lastIconAngle && lastIconAngle < 90 && 270 < nextIconAngle && nextIconAngle < 360) {
                nextIconAngle -= 360;
            }

            if (0 < nextIconAngle && nextIconAngle < 90 && 270 < lastIconAngle && lastIconAngle < 360) {
                lastIconAngle -= 360;
            }

            //Erstellt die Animation und setzt die Winkel angaben
            //Der Pfeil soll von lastIconAngle nach nextIconAngle drehen
            RotateAnimation ra = new RotateAnimation(
                    lastIconAngle,
                    nextIconAngle,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);


            //Die Animation soll 210ms dauern
            ra.setDuration(210);
            ra.setFillAfter(true);


            RowObject currentRowObject = rowObjects[i];
            currentRowObject.getIcon().setVisibility(View.VISIBLE);
            currentRowObject.getIconBlack().setVisibility(View.INVISIBLE);
            currentRowObject.setLastIconAngle(currentCompassDegree);

            //Start the animation
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
     * Sobald der GPS-Sensor deaktiviert wird, soll die ListView keien Infos mehr anzeigen
     * d.h. die Pfeile werden grau und die Entfernungsangaben werden nicht mehr angezeigt
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

