package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sahilsa on 25/5/17.
 */

public class UserAppointment implements Serializable{
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("shop_id")
    @Expose
    public String shopId;
    @SerializedName("barber_id")
    @Expose
    public String barberId;
    @SerializedName("payment_method")
    @Expose
    public String paymentMethod;
    @SerializedName("appointment_date")
    @Expose
    public String appointmentDate;
    @SerializedName("customer_name")
    @Expose
    public String customerName;
    @SerializedName("shop_name")
    @Expose
    public String shopName;
    @SerializedName("barber_name")
    @Expose
    public String barberName;
    @SerializedName("customer_id")
    @Expose
    public String customerId;
    @SerializedName("modified_date")
    @Expose
    public String modifiedDate;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("payment_status")
    @Expose
    public String paymentStatus;
    @SerializedName("appointment_status")
    @Expose
    public String appointmentStatus;
    @SerializedName("services")
    @Expose
    public List<Service> services = null;
    @SerializedName("__v")
    @Expose
    public Integer v;
}
