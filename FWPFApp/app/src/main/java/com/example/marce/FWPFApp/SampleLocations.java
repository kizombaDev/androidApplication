package com.example.marce.FWPFApp;

import android.location.Location;

/**
 * In dieser Klasse sind Sample GPS Daten hinterlegt
 * Diese werden zum Testen verwendet.
 *
 * Datei: SampleLocations  Autor: Marcel
 * Datum: 22.12  Version: <Versionsnummer>
 * Historie:
 * 20.12: Marcel Hinzufügen der GPS-Daten
 */

public class SampleLocations {

    public static Location getBerlin() {
        Location locBerlin = new Location("");
        locBerlin.setLatitude(52.520007);
        locBerlin.setLongitude(13.404954);
        return locBerlin;
    }

    public static Location getRoma() {
        Location locBerlin = new Location("");
        locBerlin.setLatitude(41.895623);
        locBerlin.setLongitude(12.482269);
        return locBerlin;
    }

    public static Location getLondon() {
        Location locBerlin = new Location("");
        locBerlin.setLatitude(51.5085300);
        locBerlin.setLongitude(-0.1257400);
        return locBerlin;
    }

    public static Location getNuernberg() {
        Location locBerlin = new Location("");
        locBerlin.setLatitude(49.4500506);
        locBerlin.setLongitude(11.077551);
        return locBerlin;
    }
}
