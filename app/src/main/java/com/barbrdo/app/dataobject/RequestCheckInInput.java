package com.barbrdo.app.dataobject;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sahilsa on 11/9/17.
 */

public class RequestCheckInInput {
    @SerializedName("request_check_in")
    public String requestCheckIn;
    @SerializedName("request_cancel_on")
    public String requestCancelOn;
}
