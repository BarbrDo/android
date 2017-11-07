package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BarberRequest implements Serializable{
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("updatedAt")
    @Expose
    public String updatedAt;
    @SerializedName("createdAt")
    @Expose
    public String createdAt;
    @SerializedName("shop_id")
    @Expose
    public String shopId;
    @SerializedName("chair_id")
    @Expose
    public String chairId;
    @SerializedName("chair_type")
    @Expose
    public String chairType;
    @SerializedName("shop_percentage")
    @Expose
    public int shopPercentage;
    @SerializedName("barber_percentage")
    @Expose
    public int barberPercentage;
    @SerializedName("requested_by")
    @Expose
    public String requestedBy;
    @SerializedName("barber_id")
    @Expose
    public String barberId;
    @SerializedName("booking_date")
    @Expose
    public String bookingDate;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("__v")
    @Expose
    public int v;
    @SerializedName("amount")
    @Expose
    public float amount;
}