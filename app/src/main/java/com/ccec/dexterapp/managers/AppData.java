package com.ccec.dexterapp.managers;

import android.location.Location;

import com.ccec.dexterapp.entities.Vehicle;

/**
 * Created by manish on 11/11/16.
 */

public class AppData {
    //updated from profile
    public static String serviceType = "Car";

    public static String selectedLoc = "";
    public static Location selectedCordLoc = null;
    public static boolean fabVisible = false;

    public static int selectedIndex = 0;

    public static Vehicle currentVeh = null;
    public static String currentImagePath = null;
}
