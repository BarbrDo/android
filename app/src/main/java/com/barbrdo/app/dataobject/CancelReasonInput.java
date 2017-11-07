package com.barbrdo.app.dataobject;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sahilsa on 19/8/17.
 */

public class CancelReasonInput {
    @SerializedName("cancel_reason")
    public String cancelReason;
    @SerializedName("request_cancel_on")
    public String requestCancelOn;
}
