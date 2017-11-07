package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransactionResponse implements Serializable{
@SerializedName("orderId")
@Expose
public String orderId;
@SerializedName("packageName")
@Expose
public String packageName;
@SerializedName("productId")
@Expose
public String productId;
@SerializedName("purchaseTime")
@Expose
public String purchaseTime;
@SerializedName("purchaseState")
@Expose
public Integer purchaseState;
@SerializedName("developerPayload")
@Expose
public String developerPayload;
@SerializedName("purchaseToken")
@Expose
public String purchaseToken;

}