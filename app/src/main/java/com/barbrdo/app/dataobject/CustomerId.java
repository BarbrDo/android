package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CustomerId implements Serializable{
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("ratings")
    @Expose
    public List<Rating> ratings = null;
    @SerializedName("picture")
    @Expose
    public String picture;
}