package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BarberHome extends BaseModel implements Serializable {
    @SerializedName("associateShops")
    @Expose
    public List<AssociateShop> associateShops = new ArrayList<>();
    @SerializedName("revenue")
    @Expose
    public Revenue revenue;
    @SerializedName("services")
    @Expose
    public List<Service> services;
    @SerializedName("is_online")
    @Expose
    public boolean isOnline;
    @SerializedName("appointment")
    @Expose
    public Appointment appointment;
    @SerializedName("online_with_shop")
    @Expose
    public OnlineWithShop onlineWithShop;

    public class AssociateShop implements Comparable<AssociateShop>, Serializable {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("shop_id")
        @Expose
        public String shopId;
        @SerializedName("barber_id")
        @Expose
        public String barberId;
        @SerializedName("shopInfo")
        @Expose
        public List<ShopInfo> shopInfo = new ArrayList<>();
        @SerializedName("is_default")
        @Expose
        public boolean isDefault;

        @Override
        public int compareTo(AssociateShop o) {
            return (o.isDefault == isDefault ? 0 : (isDefault ? -1 : 1));
        }
    }

    public class Revenue implements Serializable {
        @SerializedName("totalCuts")
        @Expose
        public int totalCuts;
        @SerializedName("revenue")
        @Expose
        public float revenue;
    }

    public class ShopInfo implements Serializable {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("license_number")
        @Expose
        public String licenseNumber;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("state")
        @Expose
        public String state;
        @SerializedName("city")
        @Expose
        public String city;
        @SerializedName("zip")
        @Expose
        public String zip;
        @SerializedName("latLong")
        @Expose
        public List<Float> latLong = null;
        @SerializedName("ratings")
        @Expose
        public List<Object> ratings = null;
        @SerializedName("payment_methods")
        @Expose
        public List<Object> paymentMethods = null;
        @SerializedName("modified_date")
        @Expose
        public String modifiedDate;
        @SerializedName("created_date")
        @Expose
        public String createdDate;
        @SerializedName("__v")
        @Expose
        public Integer v;
    }

    public class Appointment implements Serializable {
        @SerializedName("_id")
        @Expose
        public String id;
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
        public Boolean isRatingGiven;
        @SerializedName("customerInfo")
        @Expose
        public List<CustomerInfo> customerInfo = new ArrayList<>();
    }

    public class OnlineWithShop {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("name")
        @Expose
        public String name;
    }
}