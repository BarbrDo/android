package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomerAppointment extends BaseModel implements Serializable{
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {
        @SerializedName("confirm")
        @Expose
        public List<Confirm> confirm = new ArrayList<>();
        @SerializedName("complete")
        @Expose
        public List<Complete> complete = new ArrayList<>();
    }

    public class Confirm implements Serializable{
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("appointment_date")
        @Expose
        public String appointmentDate;
        @SerializedName("reach_in")
        @Expose
        public String reachIn;
        @SerializedName("barber_id")
        @Expose
        public BarberId barberId;
        @SerializedName("shop_id")
        @Expose
        public ShopId shopId;
        @SerializedName("totalPrice")
        @Expose
        public float totalPrice;
        @SerializedName("customer_id")
        @Expose
        public CustomerId customerId;
        @SerializedName("__v")
        @Expose
        public Integer v;
        @SerializedName("created_date")
        @Expose
        public String createdDate;
        @SerializedName("services")
        @Expose
        public List<Service> services = null;
        @SerializedName("appointment_status")
        @Expose
        public String appointmentStatus;
        @SerializedName("is_rating_given")
        @Expose
        public Boolean isRatingGiven;
    }

    public class Complete implements Serializable{
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("appointment_date")
        @Expose
        public String appointmentDate;
        @SerializedName("barber_id")
        @Expose
        public BarberId barberId;
        @SerializedName("shop_id")
        @Expose
        public ShopId shopId;
        @SerializedName("totalPrice")
        @Expose
        public float totalPrice;
        @SerializedName("customer_id")
        @Expose
        public CustomerId customerId;
        @SerializedName("__v")
        @Expose
        public Integer v;
        @SerializedName("cancel_by_user_type")
        @Expose
        public String cancelByUserType;
        @SerializedName("cancel_by_user_id")
        @Expose
        public String cancelByUserId;
        @SerializedName("created_date")
        @Expose
        public String createdDate;
        @SerializedName("services")
        @Expose
        public List<Service> services = null;
        @SerializedName("appointment_status")
        @Expose
        public String appointmentStatus;
        @SerializedName("is_rating_given")
        @Expose
        public Boolean isRatingGiven;
    }
}