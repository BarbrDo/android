package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Appointment extends BaseModel {

    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {
        @SerializedName("upcoming")
        @Expose
        public List<Datum> upcoming = new ArrayList<>();
        @SerializedName("complete")
        @Expose
        public List<Datum> complete = new ArrayList<>();
    }

    public class Datum implements Serializable, Comparable<Datum> {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("shop_id")
        @Expose
        public ShopId shopId;
        @SerializedName("barber_id")
        @Expose
        public BarberId barberId;
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
        public CustomerId customerId;
        @SerializedName("__v")
        @Expose
        public int v;
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
        public List<Service> services = new ArrayList<>();
        @SerializedName("is_rating_given")
        @Expose
        public boolean isRatingGiven;
        @SerializedName("rating_score")
        @Expose
        public float ratingScore;

        @Override
        public int compareTo(Datum o) {
            return appointmentDate.compareTo(o.appointmentDate);
        }
    }
}