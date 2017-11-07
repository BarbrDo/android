package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDetail extends BaseModel {
    @SerializedName("link")
    @Expose
    public String link;
    @SerializedName("user")
    @Expose
    public User user;
    @SerializedName("imagesPath")
    @Expose
    public String imagesPath;

    public class User implements Serializable {
        @SerializedName("__v")
        @Expose
        public int v;
        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;
        @SerializedName("created_date")
        @Expose
        public String createdDate;
        @SerializedName("licensed_since")
        @Expose
        public String licensedSince;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("last_name")
        @Expose
        public String lastName;
        @SerializedName("license_number")
        @Expose
        public String licenseNumber;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("mobile_number")
        @Expose
        public String mobileNumber;
        @SerializedName("user_type")
        @Expose
        public String userType;
        @SerializedName("randomString")
        @Expose
        public String randomString;
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("isActive")
        @Expose
        public Boolean isActive;
        @SerializedName("is_verified")
        @Expose
        public Boolean isVerified;
        @SerializedName("isDeleted")
        @Expose
        public Boolean isDeleted;
        @SerializedName("created")
        @Expose
        public String created;
        @SerializedName("bookings")
        @Expose
        public List<Object> bookings = null;
        @SerializedName("ratings")
        @Expose
        public List<Rating> ratings = new ArrayList<>();
        @SerializedName("payment_methods")
        @Expose
        public List<Object> paymentMethods = null;
        @SerializedName("gallery")
        @Expose
        public List<Gallery> gallery = new ArrayList<>();
        @SerializedName("picture")
        @Expose
        public String picture;
        @SerializedName("barber")
        @Expose
        public List<Barber> barber = new ArrayList<>();
        @SerializedName("shop")
        @Expose
        public List<Shop> shop = new ArrayList<>();
        @SerializedName("appointments")
        @Expose
        public List<UserAppointment> appointments = new ArrayList<>();
        @SerializedName("radius_search")
        @Expose
        public String radiusSearch;
        @SerializedName("bio")
        @Expose
        public String bio;
        @SerializedName("shop_info")
        @Expose
        public BarberShopId barberShopId;
        @SerializedName("is_online")
        @Expose
        public boolean isOnline;
        @SerializedName("subscription")
        @Expose
        public List<Subscribe> subscribe = new ArrayList<>();
        @SerializedName("subscription_start_date")
        @Expose
        public String subscriptionStartDate;
        @SerializedName("subscription_end_date")
        @Expose
        public String subscriptionEndDate;
        @SerializedName("subscription_price")
        @Expose
        public float subscriptionPrice;
        @SerializedName("subscription_pay_id")
        @Expose
        public String subscriptionPayId;
        @SerializedName("subscription_plan_name")
        @Expose
        public String subscriptionPlanName;
        @SerializedName("subscription_device_type")
        @Expose
        public String subscriptionDeviceType;
        @SerializedName("subscription_transaction_response")
        @Expose
        public List<TransactionResponse> transactionResponse;
    }
}