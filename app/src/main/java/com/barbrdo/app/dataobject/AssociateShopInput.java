package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AssociateShopInput {
    @SerializedName("shops")
    @Expose
    public List<Shop> shops = new ArrayList<>();

    public class Shop {
        @SerializedName("shop_id")
        @Expose
        public String shopId;
    }
}