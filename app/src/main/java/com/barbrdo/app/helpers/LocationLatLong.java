package com.barbrdo.app.helpers;

import android.util.Log;

import com.barbrdo.app.dataobject.PlaceDetails;
import com.barbrdo.app.utils.Constants;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LocationLatLong {
    private static final String LOG_TAG = "LocationLatLong";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/details";
    private static final String OUT_JSON = "/json";
    public String googleApiKey = Constants.GoogleAPI.GOOGLE_API_KEY;

    public LocationLatLong() {
    }

    public PlaceDetails getLatLong(String placeId) {
        PlaceDetails placeDetails = new PlaceDetails();
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE
                    + OUT_JSON);
            sb.append("?placeid=" + placeId);
            sb.append("&key=" + googleApiKey);

            Log.e("", "Url:" + sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        Log.e("Lat Long Response", jsonResults.toString());
        Gson gson = new Gson();
        placeDetails = gson.fromJson(jsonResults.toString(), PlaceDetails.class);
        return placeDetails;
    }
}