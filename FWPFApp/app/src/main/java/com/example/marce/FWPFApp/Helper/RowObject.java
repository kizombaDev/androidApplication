package com.example.marce.FWPFApp.Helper;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.marce.FWPFApp.DataObjects.Contact;

public class RowObject {

    private Contact contact;
    private TextView distanceTextView;
    private ImageView icon;
    private float lastIconDegree;
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

    public void setLastIconDegree(float lastIconDegree) {
        this.lastIconDegree = lastIconDegree;
    }

    public float getLastIconDegree() {
        return lastIconDegree;
    }

    public void setIconBlack(ImageView iconBlack) {
        this.iconBlack = iconBlack;
    }

    public ImageView getIconBlack() {
        return iconBlack;
    }
}
