package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionPlans extends BaseModel {
    @SerializedName("data")
    @Expose
    public List<Datum> data = new ArrayList<>();

    public class Datum {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("duration")
        @Expose
        public int duration;
        @SerializedName("price")
        @Expose
        public float price;
        @SerializedName("google_id")
        @Expose
        public String googleId;
        @SerializedName("plan_type")
        @Expose
        public String planType;
        @SerializedName("is_active")
        @Expose
        public boolean isActive;
        @SerializedName("created_date")
        @Expose
        public String createdDate;
        public boolean isSelected;
    }
}