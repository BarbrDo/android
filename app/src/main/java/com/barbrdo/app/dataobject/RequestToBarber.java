package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RequestToBarber {
    @SerializedName("appointment_status")
    @Expose
    public String appointmentStatus;
    @SerializedName("totalPrice")
    @Expose
    public float totalPrice;
    @SerializedName("is_rating_given")
    @Expose
    public Boolean isRatingGiven;
    @SerializedName("appointment_date")
    @Expose
    public String appointmentDate;
    @SerializedName("services")
    @Expose
    public List<Service> services = new ArrayList<>();
    @SerializedName("customer_lat_long")
    @Expose
    public List<Float> customerLatLong = new ArrayList<>();
    @SerializedName("shop_id")
    @Expose
    public String shopId;
    @SerializedName("barber_id")
    @Expose
    public String barberId;
    @SerializedName("shop_lat_long")
    @Expose
    public List<Float> shopLatLong = new ArrayList<>();
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("customer_id")
    @Expose
    public String customerId;
    @SerializedName("reach_in")
    @Expose
    public String reachIn;
}