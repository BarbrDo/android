package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetails {
    @SerializedName("result")
    @Expose
    public Result result;
    @SerializedName("status")
    @Expose
    public String status;

    public class Result {
        @SerializedName("address_components")
        @Expose
        public List<AddressComponent> addressComponents = new ArrayList<>();
        @SerializedName("adr_address")
        @Expose
        public String adrAddress;
        @SerializedName("formatted_address")
        @Expose
        public String formattedAddress;
        @SerializedName("geometry")
        @Expose
        public Geometry geometry;
        @SerializedName("icon")
        @Expose
        public String icon;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("name")
        @Expose
        public String name;
    }

    public class AddressComponent {
        @SerializedName("long_name")
        @Expose
        public String longName;
        @SerializedName("short_name")
        @Expose
        public String shortName;
        @SerializedName("types")
        @Expose
        public List<String> types = new ArrayList<>();
    }

    public class Geometry {
        @SerializedName("location")
        @Expose
        public Location location;
    }

    public class Location {
        @SerializedName("lat")
        @Expose
        public double lat;
        @SerializedName("lng")
        @Expose
        public double lng;
    }
}