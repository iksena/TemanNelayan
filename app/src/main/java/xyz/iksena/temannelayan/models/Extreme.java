package xyz.iksena.temannelayan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Extreme extends RealmObject {
    @SerializedName("dt")
    @Expose
    public int dt;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("height")
    @Expose
    public double height;
    @SerializedName("type")
    @Expose
    public String type;

    public int getDt() {
        return dt;
    }

    public Extreme setDt(int dt) {
        this.dt = dt;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Extreme setDate(String date) {
        this.date = date;
        return this;
    }

    public double getHeight() {
        return height;
    }

    public Extreme setHeight(double height) {
        this.height = height;
        return this;
    }

    public String getType() {
        return type;
    }

    public Extreme setType(String type) {
        this.type = type;
        return this;
    }
}
