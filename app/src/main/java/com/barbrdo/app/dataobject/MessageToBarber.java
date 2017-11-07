package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageToBarber implements Serializable{
@SerializedName("customerInfo")
@Expose
public CustomerInfo customerInfo;
@SerializedName("text")
@Expose
public String text;
}