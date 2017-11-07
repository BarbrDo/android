package com.barbrdo.app.helpers;

import android.util.Log;

import com.barbrdo.app.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GoogleMapRoute {
    private static final String LOG_TAG = "GoogleMapRoute";
    private static final String DIRECTIONS_API_BASE = "http://maps.googleapis.com/maps/api/directions/json";
    public String googleApiKey = Constants.GoogleAPI.GOOGLE_API_KEY;

    public GoogleMapRoute() {
    }

    public String getRoutePath(String lat1, String lon1, String lat2, String lon2) {
        String route = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(DIRECTIONS_API_BASE);
            sb.append("?origin=" + lat1+","+lon1);
            sb.append("&destination=" + lat2+","+lon2);
            sb.append("&sensor=false");

            Log.e("", "Url:" + sb.toString());

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return route;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return route;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            Log.e("Place Response", "" + jsonObj.toString());
            JSONArray jsonArrayRoutes= jsonObj.getJSONArray("routes");
            JSONObject jsonObjectRoutes = (JSONObject) jsonArrayRoutes.get(0);
            JSONObject jsonObjectOverViewPolyline = jsonObjectRoutes.getJSONObject("overview_polyline");
            route = jsonObjectOverViewPolyline.getString("points");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return route;
    }
}