package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BarberSlots extends BaseModel{
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {
        @SerializedName("morning")
        @Expose
        public List<TimeSlots> morning = new ArrayList<>();
        @SerializedName("afternoon")
        @Expose
        public List<TimeSlots> afternoon = new ArrayList<>();
        @SerializedName("evening")
        @Expose
        public List<TimeSlots> evening = new ArrayList<>();
    }

    public class TimeSlots {
        @SerializedName("time")
        @Expose
        public String time;
        @SerializedName("isAvailable")
        @Expose
        public boolean isAvailable;
        public boolean isSelected;

        public TimeSlots(String time, boolean isSelected) {
            this.time = time;
            this.isSelected = isSelected;
        }
    }
}