package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountUpdate extends BaseModel {
    @SerializedName("user")
    @Expose
    public UserDetail.User user;
}