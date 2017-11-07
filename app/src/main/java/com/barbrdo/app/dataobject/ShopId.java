package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShopId implements Serializable {
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("zip")
    @Expose
    public String zip;
    @SerializedName("gallery")
    @Expose
    public List<Gallery> gallery = null;
    @SerializedName("latLong")
    @Expose
    public List<Float> latLong = null;
}