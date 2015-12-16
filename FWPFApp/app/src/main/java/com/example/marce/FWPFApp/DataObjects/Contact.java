package com.example.marce.FWPFApp.DataObjects;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {

    private String name;
    private Location location;

    public Contact(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float distanceToLocation(Location secondLocation) {
        if (location == null)
            return 0;

        return secondLocation.distanceTo(location);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(location, flags);
    }

    public static final Parcelable.Creator<Contact> CREATOR
            = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in.readString(), (Location)in.readParcelable(Location.class.getClassLoader()));
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
