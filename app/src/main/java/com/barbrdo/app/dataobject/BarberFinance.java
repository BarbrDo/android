package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BarberFinance extends BaseModel{
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {
        @SerializedName("totalSale")
        @Expose
        public List<TotalSale> totalSale = new ArrayList<>();
        @SerializedName("monthSale")
        @Expose
        public List<MonthSale> monthSale = new ArrayList<>();
        @SerializedName("weekSale")
        @Expose
        public List<WeekSale> weekSale = new ArrayList<>();
        @SerializedName("custom")
        @Expose
        public List<Custom> custom = new ArrayList<>();
    }

    public class MonthSale {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("total_sale")
        @Expose
        public float totalSale;
    }

    public class TotalSale {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("total_sale")
        @Expose
        public float totalSale;
    }

    public class WeekSale {
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("total_sale")
        @Expose
        public float totalSale;
    }

    public class Custom implements Comparable<Custom>{
        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("appointments")
        @Expose
        public float appointments;
        @SerializedName("sale")
        @Expose
        public float sale;
        @SerializedName("appointment_Date")
        @Expose
        public String appointmentDate;

        @Override
        public int compareTo(Custom o) {
            return appointmentDate.compareTo(o.appointmentDate);
        }
    }
}