package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactBarberInput {
    @SerializedName("minutes")
    @Expose
    public String minutes;
    @SerializedName("appointment_id")
    @Expose
    public String appointmentId;
    @SerializedName("appointment_date")
    @Expose
    public String appointmentDate;
    @SerializedName("barber_id")
    @Expose
    public String barberId;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("email")
    @Expose
    public String email;
}