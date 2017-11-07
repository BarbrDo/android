package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sahilsa on 16/5/17.
 */

public class UpdateAccountInput {
    @SerializedName("mobile_number")
    public String mobileNumber;
    @SerializedName("first_name")
    public String firstName;
    @SerializedName("last_name")
    public String lastName;
    public String confirm;
    public String password;
    @SerializedName("radius_search")
    public String radiusSearch;
    @SerializedName("bio")
    public String bio;
    @SerializedName("licensed_since")
    public String licensedSince;
}
