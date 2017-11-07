package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Service implements Serializable {
    @SerializedName("service_id")
    @Expose
    public String serviceId;
    @SerializedName("price")
    @Expose
    public float price;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("_id")
    public String id;
    public boolean isSelected;
    public boolean status;
    @SerializedName("is_optional")
    public boolean isOptional;
    public Service(String serviceId, String name, float price) {
        this.serviceId = serviceId;
        this.name = name;
        this.price = price;
    }
}