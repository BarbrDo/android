package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Rating implements Serializable{
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("comments")
    @Expose
    public String comments;
    @SerializedName("score")
    @Expose
    public Float score;
    @SerializedName("rated_by")
    @Expose
    public String ratedBy;
    @SerializedName("rated_by_name")
    @Expose
    public String ratedByName;
    @SerializedName("appointment_date")
    @Expose
    public String appointmentDate;
    @SerializedName("picture")
    @Expose
    public String picture;
}