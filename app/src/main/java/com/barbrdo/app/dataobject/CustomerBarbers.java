package com.barbrdo.app.dataobject;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomerBarbers {
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("data")
    @Expose
    public List<Datum> data = null;

    public class Datum implements Serializable{
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
        @SerializedName("picture")
        @Expose
        public String picture;
        @SerializedName("mobile_number")
        @Expose
        public String mobileNumber;
        @SerializedName("gallery")
        @Expose
        public List<Gallery> gallery = new ArrayList<>();
        @SerializedName("ratings")
        @Expose
        public List<Rating> ratings = new ArrayList<>();
        @SerializedName("barber_services")
        @Expose
        public List<Service> barberServices = new ArrayList<>();
        @SerializedName("is_available")
        @Expose
        public Boolean isAvailable;
        @SerializedName("is_online")
        @Expose
        public Boolean isOnline;
        @SerializedName("distance")
        @Expose
        public float distance;
        @SerializedName("units")
        @Expose
        public String units;
        @SerializedName("barber_shops")
        @Expose
        public BarberShops barberShops;
        @SerializedName("is_favourite")
        @Expose
        public Boolean isFavourite;

//        @Override
//        public int compareTo(@NonNull Datum o) {
//            return isFavourite.compareTo(o.isFavourite);
//        }
    }
}