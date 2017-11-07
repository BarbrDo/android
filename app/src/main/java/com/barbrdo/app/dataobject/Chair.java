package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chair implements Serializable {

    @SerializedName("isActive")
    @Expose
    public Boolean isActive;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("modified_date")
    @Expose
    public String modifiedDate;
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("availability")
    @Expose
    public String availability;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("booking_start")
    @Expose
    public String bookingStart;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("booking_end")
    @Expose
    public String bookingEnd;
    @SerializedName("amount")
    @Expose
    public float amount;
    @SerializedName("barber_id")
    @Expose
    public String barberId;
    @SerializedName("barber_name")
    @Expose
    public String barberName;
    @SerializedName("shop_percentage")
    @Expose
    public int shopPercentage;
    @SerializedName("barber_percentage")
    @Expose
    public int barberPercentage;
    @SerializedName("barber_picture")
    @Expose
    public String barberPicture;
    @SerializedName("barber")
    @Expose
    public List<BarberId> barber = new ArrayList<>();
    @SerializedName("barberRequest")
    @Expose
    public List<BarberRequest> barberRequest = new ArrayList<>();
}