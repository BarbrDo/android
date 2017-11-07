package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AppointmentInput implements Serializable{
    @SerializedName("appointment_id")
    @Expose
    public String appointmentId;
    @SerializedName("shop_id")
    @Expose
    public String shopId;
    @SerializedName("barber_id")
    @Expose
    public String barberId;
    @SerializedName("payment_method")
    @Expose
    public String paymentMethod;
    @SerializedName("services")
    @Expose
    public List<Service> services = null;
    @SerializedName("appointment_date")
    @Expose
    public String appointmentDate;
    public float totalPrice;
    public String token;
    public float amount;
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