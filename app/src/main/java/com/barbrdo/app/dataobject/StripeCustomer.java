package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StripeCustomer implements Serializable{
    @SerializedName("subscriptions")
    @Expose
    public Subscriptions subscriptions;
    @SerializedName("sources")
    @Expose
    public Sources sources;
    @SerializedName("shipping")
    @Expose
    public Object shipping;
    @SerializedName("metadata")
    @Expose
    public Metadata metadata;
    @SerializedName("livemode")
    @Expose
    public Boolean livemode;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("discount")
    @Expose
    public Object discount;
    @SerializedName("description")
    @Expose
    public Object description;
    @SerializedName("delinquent")
    @Expose
    public Boolean delinquent;
    @SerializedName("default_source")
    @Expose
    public Object defaultSource;
    @SerializedName("currency")
    @Expose
    public Object currency;
    @SerializedName("created")
    @Expose
    public Integer created;
    @SerializedName("account_balance")
    @Expose
    public Integer accountBalance;
    @SerializedName("object")
    @Expose
    public String object;
    @SerializedName("id")
    @Expose
    public String id;

    public class Subscriptions implements Serializable{
        @SerializedName("url")
        @Expose
        public String url;
        @SerializedName("total_count")
        @Expose
        public Integer totalCount;
        @SerializedName("has_more")
        @Expose
        public Boolean hasMore;
        @SerializedName("data")
        @Expose
        public List<Object> data = null;
        @SerializedName("object")
        @Expose
        public String object;
    }

    public class Sources implements Serializable{
        @SerializedName("url")
        @Expose
        public String url;
        @SerializedName("total_count")
        @Expose
        public Integer totalCount;
        @SerializedName("has_more")
        @Expose
        public Boolean hasMore;
        @SerializedName("data")
        @Expose
        public List<Object> data = null;
        @SerializedName("object")
        @Expose
        public String object;
    }

    public class Metadata implements Serializable{
        @SerializedName("mobile_number")
        @Expose
        public String mobileNumber;
        @SerializedName("last_name")
        @Expose
        public String lastName;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("user_type")
        @Expose
        public String userType;
    }
}