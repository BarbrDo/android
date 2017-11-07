package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Dist implements Serializable {
    @SerializedName("calculated")
    @Expose
    public Float calculated;
    @SerializedName("location")
    @Expose
    public List<Float> location = null;
}