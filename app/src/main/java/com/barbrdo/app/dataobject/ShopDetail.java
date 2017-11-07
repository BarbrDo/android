package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopDetail {
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("latLong")
        @Expose
        public List<Float> latLong = null;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("gallery")
        @Expose
        public List<Object> gallery = null;
        @SerializedName("barber")
        @Expose
        public List<BarberId> barber = null;
        @SerializedName("state")
        @Expose
        public String state;
        @SerializedName("city")
        @Expose
        public String city;
    }
}