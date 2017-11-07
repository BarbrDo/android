package com.barbrdo.app.dataobject;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sahilsa on 17/8/17.
 */

public class MessageInput {
    public String text;
    @SerializedName("barber_id")
    public String barberId;
    @SerializedName("customer_id")
    public String customerId;
}
