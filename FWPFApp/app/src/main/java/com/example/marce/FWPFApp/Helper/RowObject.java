package com.example.marce.FWPFApp.Helper;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.marce.FWPFApp.DataObjects.Contact;

/**
 * Die Datenobjekt speichert Informationen zu jeder Zeile der ListView
 * Z.B. Contact und aktueller Winkel des roten Pfeils
 *
 * * Datei: ContactArrayAdapter  Autor: Marcel
 * Datum: 22.12  Version: <Versionsnummer>
 * Historie:
 * 18.12: Marcel Erstellung der Klasse
 */

public class RowObject {

    private Contact contact;
    private TextView distanceTextView;
    private ImageView icon;
    private float lastIconAngle;
    private ImageView iconBlack;

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setDistanceTextView(TextView distanceTextView) {
        this.distanceTextView = distanceTextView;
    }

    public Contact getContact() {
        return contact;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public TextView getDistanceTextView() {
        return distanceTextView;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setLastIconAngle(float lastIconDegree) {
        this.lastIconAngle = lastIconDegree;
    }

    public float getLastIconAngle() {
        return lastIconAngle;
    }

    public void setIconBlack(ImageView iconBlack) {
        this.iconBlack = iconBlack;
    }

    public ImageView getIconBlack() {
        return iconBlack;
    }
}
