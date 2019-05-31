package xyz.iksena.temannelayan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

public class Solunar extends RealmObject {

    public Date createdAt = new Date();
    @SerializedName("sunRise")
    @Expose
    public String sunRise;
    @SerializedName("sunTransit")
    @Expose
    public String sunTransit;
    @SerializedName("sunSet")
    @Expose
    public String sunSet;
    @SerializedName("moonRise")
    @Expose
    public String moonRise;
    @SerializedName("moonTransit")
    @Expose
    public String moonTransit;
    @SerializedName("moonUnder")
    @Expose
    public String moonUnder;
    @SerializedName("moonSet")
    @Expose
    public String moonSet;
    @SerializedName("moonPhase")
    @Expose
    public String moonPhase;
    @SerializedName("moonIllumination")
    @Expose
    public double moonIllumination;
    @SerializedName("sunRiseDec")
    @Expose
    public double sunRiseDec;
    @SerializedName("sunTransitDec")
    @Expose
    public double sunTransitDec;
    @SerializedName("sunSetDec")
    @Expose
    public double sunSetDec;
    @SerializedName("moonRiseDec")
    @Expose
    public double moonRiseDec;
    @SerializedName("moonSetDec")
    @Expose
    public double moonSetDec;
    @SerializedName("moonTransitDec")
    @Expose
    public double moonTransitDec;
    @SerializedName("moonUnderDec")
    @Expose
    public double moonUnderDec;
    @SerializedName("minor1StartDec")
    @Expose
    public double minor1StartDec;
    @SerializedName("minor1Start")
    @Expose
    public String minor1Start;
    @SerializedName("minor1StopDec")
    @Expose
    public double minor1StopDec;
    @SerializedName("minor1Stop")
    @Expose
    public String minor1Stop;
    @SerializedName("minor2StartDec")
    @Expose
    public double minor2StartDec;
    @SerializedName("minor2Start")
    @Expose
    public String minor2Start;
    @SerializedName("minor2StopDec")
    @Expose
    public double minor2StopDec;
    @SerializedName("minor2Stop")
    @Expose
    public String minor2Stop;
    @SerializedName("major1StartDec")
    @Expose
    public double major1StartDec;
    @SerializedName("major1Start")
    @Expose
    public String major1Start;
    @SerializedName("major1StopDec")
    @Expose
    public double major1StopDec;
    @SerializedName("major1Stop")
    @Expose
    public String major1Stop;
    @SerializedName("major2StartDec")
    @Expose
    public double major2StartDec;
    @SerializedName("major2Start")
    @Expose
    public String major2Start;
    @SerializedName("major2StopDec")
    @Expose
    public double major2StopDec;
    @SerializedName("major2Stop")
    @Expose
    public String major2Stop;
    @SerializedName("dayRating")
    @Expose
    public int dayRating;
    @SerializedName("hourlyRating")
    @Expose
    public HourlyRating hourlyRating;


    public int getDayRating() {
        return dayRating;
    }

    public Solunar setDayRating(int dayRating) {
        this.dayRating = dayRating;
        return this;
    }

    public HourlyRating getHourlyRating() {
        return hourlyRating;
    }

    public Solunar setHourlyRating(HourlyRating hourlyRating) {
        this.hourlyRating = hourlyRating;
        return this;
    }

}
