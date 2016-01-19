package com.example.marce.FWPFApp.Helper;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.marce.FWPFApp.DataObjects.Contact;

/**
 * This dataobject contains the data of each row the contact listView
 * for example the contact and the current angle of the red arrow
 * <p/>
 * File: ContactArrayAdapter  Autor: Marcel
 * Datum: 22.12  Version: <Versionsnummer>
 * Historie:
 * 18.12: Marcel creats the class
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
