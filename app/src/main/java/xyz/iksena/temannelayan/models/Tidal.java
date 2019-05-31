package xyz.iksena.temannelayan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Tidal extends RealmObject {

    public Date createdAt = new Date();
    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("callCount")
    @Expose
    public int callCount;
    @SerializedName("copyright")
    @Expose
    public String copyright;
    @SerializedName("requestLat")
    @Expose
    public double requestLat;
    @SerializedName("requestLon")
    @Expose
    public double requestLon;
    @SerializedName("responseLat")
    @Expose
    public double responseLat;
    @SerializedName("responseLon")
    @Expose
    public double responseLon;
    @SerializedName("atlas")
    @Expose
    public String atlas;
    @SerializedName("station")
    @Expose
    public String station;
    @SerializedName("heights")
    @Expose
    public RealmList<Height> heights = null;
    @SerializedName("extremes")
    @Expose
    public RealmList<Extreme> extremes = null;

    public int getStatus() {
        return status;
    }

    public Tidal setStatus(int status) {
        this.status = status;
        return this;
    }

    public double getRequestLat() {
        return requestLat;
    }

    public Tidal setRequestLat(double requestLat) {
        this.requestLat = requestLat;
        return this;
    }

    public double getRequestLon() {
        return requestLon;
    }

    public Tidal setRequestLon(double requestLon) {
        this.requestLon = requestLon;
        return this;
    }

    public double getResponseLat() {
        return responseLat;
    }

    public Tidal setResponseLat(double responseLat) {
        this.responseLat = responseLat;
        return this;
    }

    public double getResponseLon() {
        return responseLon;
    }

    public Tidal setResponseLon(double responseLon) {
        this.responseLon = responseLon;
        return this;
    }

    public String getStation() {
        return station;
    }

    public Tidal setStation(String station) {
        this.station = station;
        return this;
    }

    public RealmList<Height> getHeights() {
        return heights;
    }

    public Tidal setHeights(RealmList<Height> heights) {
        this.heights = heights;
        return this;
    }

    public RealmList<Extreme> getExtremes() {
        return extremes;
    }

    public Tidal setExtremes(RealmList<Extreme> extremes) {
        this.extremes = extremes;
        return this;
    }

}
