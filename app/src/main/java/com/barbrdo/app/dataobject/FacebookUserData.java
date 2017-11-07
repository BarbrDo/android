package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sahilsa on 3/2/17.
 */

public class FacebookUserData {
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("link")
    @Expose
    public String link;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("phone")
    @Expose
    public String phone;
}
