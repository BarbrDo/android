package com.barbrdo.app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.barbrdo.app.dataobject.UserDetail;
import com.google.gson.Gson;

/**
 * Created by Sahil Salgotra on 18/2/16.
 */

public class SessionManager {
    public static final String REMEMBER_ME = "remember_me";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String IS_LOGGED_IN = "log_in";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String AUTHORIZATION_TOKEN = "authorization_token";
    public static final String DEVICE_LATITUDE = "device_latitude";
    public static final String DEVICE_LONGITUDE = "device_longitude";
    public static final String USER_TYPE = "user_type";
    public static final String USER_ID = "user_id";
    public static final String BASE_URL = "base_url";
    private static final String PREF_NAME = "SystemPreference";
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setRememberMe(String username, String password) {
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    public boolean getIsLoggedIn() {
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public void setIsLoggedIn(boolean logInStatus) {
        editor.putBoolean(IS_LOGGED_IN, logInStatus);
        editor.commit();
    }

    public void saveUser(UserDetail user) {
        Editor prefsEditor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
    }

    public UserDetail getUser() {
        Gson gson = new Gson();
        String json = pref.getString("MyObject", "");
        return gson.fromJson(json, UserDetail.class);
    }

    public void clearRememberMe() {
        editor.clear();
        editor.commit();
    }

    public void setAuthorizationToken(String deviceToken) {
        editor.putString(AUTHORIZATION_TOKEN, deviceToken);
        editor.commit();
    }

    public String getAuthorizationToken() {
        return pref.getString(AUTHORIZATION_TOKEN, "");
    }


    public void setDeviceToken(String deviceToken) {
        editor.putString(DEVICE_TOKEN, deviceToken);
        editor.commit();
    }

    public String getDeviceToken() {
        return pref.getString(DEVICE_TOKEN, "");
    }

    public void setUserId(String userId) {
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public void setImageBaseUrl(String url) {
        editor.putString(BASE_URL, url);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(USER_ID, "");
    }

    public String getImageBaseUrl() {
        return pref.getString(BASE_URL, "");
    }

    public void setUserType(String userType) {
        editor.putString(USER_TYPE, userType);
        editor.commit();
    }

    public String getUserType() {
        return pref.getString(USER_TYPE, "");
    }

    public void setDeviceLatitude(String latitude) {
        editor.putString(DEVICE_LATITUDE, latitude);
        editor.commit();
    }

    public String getDeviceLatitude() {
        return pref.getString(DEVICE_LATITUDE, "");
//        return "40.658801";
    }

    public void setDeviceLongitude(String longitude) {
        editor.putString(DEVICE_LONGITUDE, longitude);
        editor.commit();
    }

    public String getDeviceLongitude() {
        return pref.getString(DEVICE_LONGITUDE, "");
//        return "-74.1063776";
    }

//    40.658801, -74.1063776
}
