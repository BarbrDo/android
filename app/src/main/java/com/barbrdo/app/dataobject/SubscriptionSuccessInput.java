package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionSuccessInput {
    @SerializedName("plan_id")
    @Expose
    public String planId;
    @SerializedName("tranaction_response")
    @Expose
    public List<TransactionResponse> transactionResponse = new ArrayList<>();
}