package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BarberId implements Serializable {
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("ratings")
    @Expose
    public List<Rating> ratings = new ArrayList<>();
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("distance")
    @Expose
    public float distance;
    @SerializedName("shop_id")
    @Expose
    public String shopId;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("picture")
    @Expose
    public String picture;
    @SerializedName("chair_id")
    @Expose
    public String chairId;
    @SerializedName("chair_name")
    @Expose
    public String chairName;
    @SerializedName("chair_type")
    @Expose
    public String chairType;
    @SerializedName("chair_amount")
    @Expose
    public String chairAmount;
    @SerializedName("chair_shop_percentage")
    @Expose
    public int chairShopPercentage;
    @SerializedName("chair_barber_percentage")
    @Expose
    public int chairBarberPercentage;
}