package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BarberServices {
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("data")
    @Expose
    public List<Datum> data = new ArrayList<>();

    public class Datum implements Serializable{
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("service_id")
        @Expose
        public String serviceId;
        @SerializedName("barber_id")
        @Expose
        public String barberId;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("price")
        @Expose
        public float price;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("modified")
        @Expose
        public String modified;
        @SerializedName("modified_date")
        @Expose
        public String modifiedDate;
        @SerializedName("created_date")
        @Expose
        public String createdDate;
        public boolean isSelected;

        public Datum(String name, float price) {
            this.name = name;
            this.price = price;
        }
    }
}