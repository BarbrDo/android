package com.barbrdo.app.dataobject;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by sahilsa on 18/2/16.
 */
public class AppInstance {

    private static final String PREFS_NAME = "com.app.maxerrand";
    private static final String PREFS_KEY = "appInstance";

    public String successMessage;
    public UserDetail userDetail;
    public ShopDetail shopDetail;
    public GalleryUpdate galleryUpdate;
    public AccountUpdate accountUpdate;
    public UserProfile userProfile;
    public String message;
    public UserProfile userProfileShop;
    public BarberFinance barberFinance;
    public ShopFinance shopFinance;
    public CustomerBarbers customerBarbers;
    public CustomerRequestDetails customerRequestDetails;
    public BarberHome barberHome;
    public CustomerAppointment customerAppointment;
    public BarberDetail barberDetail;
    public SearchShops searchShops;
    public States states;
    public SubscriptionPlans subscriptionPlans;

    /**
     * private constructor
     */
    private AppInstance() {
    }

    /**
     * access point for the unique instance of the singleton
     */
    public static AppInstance getInstance() {
        return AppInstanceHolder.instance;
    }

    static public AppInstance create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of
        // the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, AppInstance.class);
    }

    static public void restoreDatabaseState(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of
        // the state
        Gson gson = new Gson();
        gson.fromJson(serializedData, AppInstance.class);
    }

    // // Save the serialized database
    // into shared preference
    public static void saveSerialDatabase(Context context) {
        AppInstance appInstance = AppInstance.getInstance();

        SharedPreferences preferencesReader = context.getSharedPreferences(
                AppInstance.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesReader.edit();
        editor.putString(AppInstance.PREFS_KEY, appInstance.serialize());
        editor.commit();

    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Holder
     */
    private static class AppInstanceHolder {
        /**
         * unique instance not pre initialized
         */
        private static final AppInstance instance = new AppInstance();
    }
}
