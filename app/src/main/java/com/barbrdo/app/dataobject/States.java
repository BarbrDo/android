package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class States extends BaseModel {

    @SerializedName("data")
    @Expose
    public List<Datum> data = new ArrayList<>();

    public class Datum {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("abbreviation")
        @Expose
        public String abbreviation;

        public Datum(String id, String name, String abbreviation) {
            this.id = id;
            this.name = name;
            this.abbreviation = abbreviation;
        }
    }
}