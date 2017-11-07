package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Barber implements Serializable {
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("mobile_number")
    @Expose
    public String mobileNumber;
    @SerializedName("license_number")
    @Expose
    public String licenseNumber;
    @SerializedName("gallery")
    @Expose
    public List<Gallery> gallery = new ArrayList<>();
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
    @SerializedName("picture")
    @Expose
    public String picture;
    @SerializedName("ratings")
    @Expose
    public List<Rating> ratings = new ArrayList<>();
}