package com.ccec.dexterapp.managers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ccec.dexterapp.Login;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by aanchalharit on 15/11/16.
 */

public class UserSessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME = "OxceanPref";
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String FIRST_TIME = "";

    public static final String TAG_id = "id";
    public static final String TAG_fullname = "fullname";
    public static final String TAG_pwd = "pwd";
    public static final String TAG_email = "email";
    public static final String TAG_profilepic = "pic";
    public static final String TAG_contact = "contact";
    public static final String TAG_location = "location";

    public static final String TAG_set1 = "set1";
    public static final String TAG_set2 = "set2";
    public static final String TAG_set3 = "set3";
    public static final String TAG_set4 = "set4";
    public static final String TAG_set5 = "set5";


    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //updated from login and signup
    public void createUserLoginSession(String uId, String uEmail, String uPwd) {
        editor.putBoolean(IS_USER_LOGIN, true);

        editor.putString(TAG_id, uId);
        editor.putString(TAG_email, uEmail);
        editor.putString(TAG_pwd, uPwd);

        editor.commit();
    }

    //updated from login and signup
    public void createUserLoginSession(String uName, String pic, String contact, String location) {
        editor.putBoolean(IS_USER_LOGIN, true);

        editor.putString(TAG_contact, contact);
        editor.putString(TAG_fullname, uName);
        editor.putString(TAG_location, location);
        editor.putString(TAG_profilepic, pic);

        editor.commit();
    }

    public void clearData() {
        editor.clear();
        editor.commit();
    }

    //updated from products fragment
    public void createUserLoginSession(String uName, String pic, String contact, String location, String set5) {
        editor.putBoolean(IS_USER_LOGIN, true);

        editor.putString(TAG_set1, uName);
        editor.putString(TAG_set2, pic);
        editor.putString(TAG_set3, contact);
        editor.putString(TAG_set4, location);
        editor.putString(TAG_set5, set5);

        editor.commit();
    }

    //alone update from splash second visit
    public void createUserLoginSession(int secondTime) {
        editor.putString(FIRST_TIME, "NO");
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(TAG_id, pref.getString(TAG_id, null));
        user.put(TAG_fullname, pref.getString(TAG_fullname, null));
        user.put(TAG_email, pref.getString(TAG_email, null));
        user.put(TAG_pwd, pref.getString(TAG_pwd, null));
        user.put(TAG_profilepic, pref.getString(TAG_profilepic, null));
        user.put(TAG_contact, pref.getString(TAG_contact, null));
        user.put(TAG_location, pref.getString(TAG_location, null));

        user.put(FIRST_TIME, pref.getString(FIRST_TIME, null));

        return user;
    }

    public HashMap<String, String> getSetDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(TAG_set1, pref.getString(TAG_set1, null));
        user.put(TAG_set2, pref.getString(TAG_set2, null));
        user.put(TAG_set3, pref.getString(TAG_set3, null));
        user.put(TAG_set4, pref.getString(TAG_set4, null));
        user.put(TAG_set5, pref.getString(TAG_set5, null));

        return user;
    }

    public void logoutUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/users/Customer/" + pref.getString(TAG_id, null));
        databaseReference.child("fcm").setValue("out");

        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, Login.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public boolean isOpened() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

}
