package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sahilsa on 23/6/17.
 */

public class ContactShopInput {
    @Expose
    @SerializedName("from_name")
    public String fromName;
    @Expose
    @SerializedName("from_user_id")
    public String fromUserId;
    @Expose
    @SerializedName("to_user_id")
    public String toUserId;
    @Expose
    @SerializedName("message")
    public String message;
}
