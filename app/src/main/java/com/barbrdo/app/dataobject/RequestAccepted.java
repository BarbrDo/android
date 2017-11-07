package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RequestAccepted implements Serializable{
    @SerializedName("customer_lat_long")
    @Expose
    public List<Float> customerLatLong = null;
    @SerializedName("shop_lat_long")
    @Expose
    public List<Float> shopLatLong = null;
    @SerializedName("barberInfo")
    @Expose
    public BarberInfo barberInfo;
    @SerializedName("appointmentInfo")
    @Expose
    public AppointmentInfo appointmentInfo;
}