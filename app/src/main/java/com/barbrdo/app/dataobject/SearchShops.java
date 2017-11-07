package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchShops extends BaseModel{
    @SerializedName("data")
    @Expose
    public List<Shop> data = new ArrayList<>();
}