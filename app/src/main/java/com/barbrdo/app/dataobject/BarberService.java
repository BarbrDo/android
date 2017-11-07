package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BarberService implements Serializable {
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("service_id")
    @Expose
    public String serviceId;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("price")
    @Expose
    public float price;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("__v")
    @Expose
    public Integer v;
}