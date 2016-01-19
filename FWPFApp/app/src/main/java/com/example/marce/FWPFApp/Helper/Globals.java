package com.example.marce.FWPFApp.Helper;

import android.os.Build;

/**
 * This class contains keys, which are used in activities and other classes
 * <p/>
 * Datei: Globals  Autor: Marcel
 * Datum: 22.12  Version: <Versionsnummer>
 * Historie:
 * 17.12: Marcel created the class
 */

public class Globals {
    public static String navigationActitivyIntend() {
        return "NavigationActivity.Intent";
    }

    public static String setttingIsUserRegistered() {
        return "Setting.isUserRegistered";
    }

    public static String setttingUserName() {
        return "Setting.userName";
    }

    public static String setttingPhoneNumber() {
        return "Setting.phoneNumber";
    }

    public static String settingUserId() {
        return "Setting.userId";
    }

    public static String settingFile() {
        return "Setting.globalSettignFile";
    }

    public static boolean isEmulator() {
        return "goldfish".equals(Build.HARDWARE);
    }
}


