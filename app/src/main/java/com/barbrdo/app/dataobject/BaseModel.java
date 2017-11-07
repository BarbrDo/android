package com.barbrdo.app.dataobject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sahilsa on 02/05/2017.
 */

public class BaseModel {
    public String msg;
    public String token;
    @SerializedName("err")
    public List<ErrorModel> errorList;
}
