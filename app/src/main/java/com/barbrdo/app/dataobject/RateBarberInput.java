package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sahilsa on 23/6/17.
 */

public class RateBarberInput {
    @Expose
    @SerializedName("barber_id")
    public String barberId;
    @Expose
    @SerializedName("appointment_id")
    public String appointmentId;
    @Expose
    @SerializedName("score")
    public float score;
    @Expose
    @SerializedName("is_favourite")
    public boolean isFavourite;
    @Expose
    @SerializedName("next_in_chair")
    public boolean nextInChair;
    @Expose
    @SerializedName("appointment_date")
    public String appointmentDate;
}
