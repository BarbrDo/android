package com.barbrdo.app.dataobject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sahilsa on 10/8/17.
 */

public class GoOnlineInput {
    @SerializedName("shop_id")
    public String shopId;
    @SerializedName("services")
    public List<Service> listServices;
}
