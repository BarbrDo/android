package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomerRequestDetails extends BaseModel implements Serializable{
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data implements Serializable{
        @SerializedName("__v")
        @Expose
        public int v;
        @SerializedName("appointment_date")
        @Expose
        public String appointmentDate;
        @SerializedName("barber_id")
        @Expose
        public String barberId;
        @SerializedName("shop_id")
        @Expose
        public String shopId;
        @SerializedName("totalPrice")
        @Expose
        public float totalPrice;
        @SerializedName("customer_id")
        @Expose
        public String customerId;
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("created_date")
        @Expose
        public String createdDate;
        @SerializedName("services")
        @Expose
        public List<Service> services = new ArrayList<>();
        @SerializedName("appointment_status")
        @Expose
        public String appointmentStatus;
        @SerializedName("is_rating_given")
        @Expose
        public boolean isRatingGiven;
    }
}