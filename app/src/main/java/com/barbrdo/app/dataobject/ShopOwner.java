package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShopOwner implements Serializable{
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("mobile_number")
    @Expose
    public String mobileNumber;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("user_type")
    @Expose
    public String userType;
    @SerializedName("device_type")
    @Expose
    public String deviceType;
    @SerializedName("device_id")
    @Expose
    public String deviceId;
    @SerializedName("randomString")
    @Expose
    public String randomString;
    @SerializedName("remark")
    @Expose
    public String remark;
    @SerializedName("is_active")
    @Expose
    public Boolean isActive;
    @SerializedName("is_verified")
    @Expose
    public Boolean isVerified;
    @SerializedName("is_deleted")
    @Expose
    public Boolean isDeleted;
    @SerializedName("modified_date")
    @Expose
    public String modifiedDate;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("gallery")
    @Expose
    public List<Object> gallery = null;
    @SerializedName("picture")
    @Expose
    public String picture;

}