package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BarberDetail extends BaseModel {
    @SerializedName("data")
    @Expose
    public List<Datum> data = new ArrayList<>();

    public class Datum {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("last_name")
        @Expose
        public String lastName;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("mobile_number")
        @Expose
        public String mobileNumber;
        @SerializedName("user_type")
        @Expose
        public String userType;
        @SerializedName("is_active")
        @Expose
        public boolean isActive;
        @SerializedName("is_verified")
        @Expose
        public boolean isVerified;
        @SerializedName("is_deleted")
        @Expose
        public boolean isDeleted;
        @SerializedName("created_date")
        @Expose
        public String createdDate;
        @SerializedName("gallery")
        @Expose
        public List<Gallery> gallery = null;
        @SerializedName("ratings")
        @Expose
        public List<Rating> ratings = null;
        @SerializedName("latLong")
        @Expose
        public List<Float> latLong = null;
        @SerializedName("is_available")
        @Expose
        public boolean isAvailable;
        @SerializedName("is_online")
        @Expose
        public boolean isOnline;
        @SerializedName("picture")
        @Expose
        public String picture;
        @SerializedName("associateShops")
        @Expose
        public List<Shop> associateShops = new ArrayList<>();
    }
}