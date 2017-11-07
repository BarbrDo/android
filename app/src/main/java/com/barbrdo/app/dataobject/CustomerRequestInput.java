package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CustomerRequestInput {

    @SerializedName("shop_id")
    @Expose
    public String shopId;
    @SerializedName("barber_id")
    @Expose
    public String barberId;
    @SerializedName("services")
    @Expose
    public List<Service> services = new ArrayList<>();
    @SerializedName("appointment_date")
    @Expose
    public String appointmentDate;
    @SerializedName("totalPrice")
    @Expose
    public String totalPrice;
    @SerializedName("reach_in")
    @Expose
    public String reachIn;
}