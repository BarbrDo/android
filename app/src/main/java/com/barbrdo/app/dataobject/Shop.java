package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Shop implements Serializable {
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("license_number")
    @Expose
    public String licenseNumber;
    @SerializedName("gallery")
    @Expose
    public List<Object> gallery = null;
    @SerializedName("chairs")
    @Expose
    public List<Chair> chairs = null;
    @SerializedName("ratings")
    @Expose
    public List<Rating> ratings = null;
    @SerializedName("payment_methods")
    @Expose
    public List<Object> paymentMethods = null;
    @SerializedName("modified_date")
    @Expose
    public String modifiedDate;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("__v")
    @Expose
    public Integer v;
    @SerializedName("latLong")
    @Expose
    public List<Float> latLong = null;
    @SerializedName("zip")
    @Expose
    public String zip;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("is_default")
    @Expose
    public boolean isDefault;
    @SerializedName("is_added")
    @Expose
    public boolean isAdded;
}