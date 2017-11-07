package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Sahil Salgotra
 * The class handles all the objects associated with the User entity
 */
public class User implements Serializable {
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("facebook")
    @Expose
    public String facebookId;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("mobile_number")
    @Expose
    public String mobileNumber;
    @SerializedName("user_type")
    @Expose
    public String userType;
    @SerializedName("license_number")
    @Expose
    public String licenseNumber;
}
