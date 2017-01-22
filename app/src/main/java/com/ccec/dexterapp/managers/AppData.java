package com.ccec.dexterapp.managers;

import android.location.Location;

import com.ccec.dexterapp.entities.Vehicle;

import java.util.List;

/**
 * Created by manish on 11/11/16.
 */

public class AppData {
    //updated from profile
    public static String serviceType = "Car";

    public static String selectedLoc = "";
    public static Location selectedCordLoc = null;
    public static boolean fabVisible = false;
    public static boolean raiseRequest = false;

    public static Object currentMap = null;

    public static int selectedIndex = 0;
    public static int selectedTab = 0;

    public static Vehicle currentVehi = null;
    public static String currentImagePath = null;

    public static Object currentVeh = null;

    public static List<Boolean> queries;

    public static int selectedItem = 0;

    public static int getSelectedItem() {
        return selectedItem;
    }

    public static void setSelectedItem(int selectedItem) {
        AppData.selectedItem = selectedItem;
    }
}
