package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Subscribe implements Serializable{
    @SerializedName("tranaction_response")
    @Expose
    public List<TransactionResponse> tranactionResponse = null;
    @SerializedName("created_date")
    @Expose
    public String createdDate;
    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("price")
    @Expose
    public float price;
    @SerializedName("end_date")
    @Expose
    public String endDate;
    @SerializedName("start_date")
    @Expose
    public String startDate;
    @SerializedName("plan_name")
    @Expose
    public String planName;
}