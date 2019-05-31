package xyz.iksena.temannelayan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Height extends RealmObject {
    @SerializedName("dt")
    @Expose
    public int dt;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("height")
    @Expose
    public double height;

    public int getDt() {
        return dt;
    }

    public Height setDt(int dt) {
        this.dt = dt;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Height setDate(String date) {
        this.date = date;
        return this;
    }

    public double getHeight() {
        return height;
    }

    public Height setHeight(double height) {
        this.height = height;
        return this;
    }
}
