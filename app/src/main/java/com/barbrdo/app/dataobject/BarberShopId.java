package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BarberShopId implements Serializable{
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("license_number")
    @Expose
    public String licenseNumber;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("zip")
    @Expose
    public String zip;
    @SerializedName("latLong")
    @Expose
    public List<Float> latLong = null;
    @SerializedName("__v")
    @Expose
    public Integer v;
    @SerializedName("modified_date")
    @Expose
    public String modifiedDate;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
}