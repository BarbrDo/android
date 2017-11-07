package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddShopInput {
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("zip")
    @Expose
    public String zip;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("street_address")
    @Expose
    public String streetAddress;
    @SerializedName("latitude")
    @Expose
    public double latitude;
    @SerializedName("longitude")
    @Expose
    public double longitude;
}