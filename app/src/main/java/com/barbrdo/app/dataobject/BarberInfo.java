package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BarberInfo implements Serializable{
    @SerializedName("barber_shops_latLong")
    @Expose
    public List<Float> barberShopsLatLong = null;
    @SerializedName("device_type")
    @Expose
    public String deviceType;
    @SerializedName("remark")
    @Expose
    public String remark;
    @SerializedName("barber_services")
    @Expose
    public List<BarberService> barberServices = null;
    @SerializedName("barber_shop_id")
    @Expose
    public String barberShopId;
    @SerializedName("licensed_since")
    @Expose
    public String licensedSince;
    @SerializedName("user_type")
    @Expose
    public String userType;
    @SerializedName("is_deleted")
    @Expose
    public Boolean isDeleted;
    @SerializedName("ratings")
    @Expose
    public List<Rating> ratings = null;
    @SerializedName("latLong")
    @Expose
    public List<Float> latLong = null;
    @SerializedName("__v")
    @Expose
    public Integer v;
    @SerializedName("is_online")
    @Expose
    public Boolean isOnline;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("gallery")
    @Expose
    public List<Gallery> gallery = null;
    @SerializedName("random_string")
    @Expose
    public String randomString;
    @SerializedName("is_active")
    @Expose
    public Boolean isActive;
    @SerializedName("device_id")
    @Expose
    public String deviceId;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("modified_date")
    @Expose
    public String modifiedDate;
    @SerializedName("is_verified")
    @Expose
    public Boolean isVerified;
    @SerializedName("picture")
    @Expose
    public String picture;
    @SerializedName("license_number")
    @Expose
    public String licenseNumber;
    @SerializedName("is_available")
    @Expose
    public Boolean isAvailable;
    @SerializedName("favourite_barber")
    @Expose
    public List<Object> favouriteBarber = null;
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("mobile_number")
    @Expose
    public String mobileNumber;
    @SerializedName("rating_score")
    @Expose
    public float ratingScore;
}