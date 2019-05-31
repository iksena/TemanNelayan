package xyz.iksena.temannelayan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class HourlyRating extends RealmObject {

    @SerializedName("0")
    @Expose
    public int _0;
    @SerializedName("1")
    @Expose
    public int _1;
    @SerializedName("2")
    @Expose
    public int _2;
    @SerializedName("3")
    @Expose
    public int _3;
    @SerializedName("4")
    @Expose
    public int _4;
    @SerializedName("5")
    @Expose
    public int _5;
    @SerializedName("6")
    @Expose
    public int _6;
    @SerializedName("7")
    @Expose
    public int _7;
    @SerializedName("8")
    @Expose
    public int _8;
    @SerializedName("9")
    @Expose
    public int _9;
    @SerializedName("10")
    @Expose
    public int _10;
    @SerializedName("11")
    @Expose
    public int _11;
    @SerializedName("12")
    @Expose
    public int _12;
    @SerializedName("13")
    @Expose
    public int _13;
    @SerializedName("14")
    @Expose
    public int _14;
    @SerializedName("15")
    @Expose
    public int _15;
    @SerializedName("16")
    @Expose
    public int _16;
    @SerializedName("17")
    @Expose
    public int _17;
    @SerializedName("18")
    @Expose
    public int _18;
    @SerializedName("19")
    @Expose
    public int _19;
    @SerializedName("20")
    @Expose
    public int _20;
    @SerializedName("21")
    @Expose
    public int _21;
    @SerializedName("22")
    @Expose
    public int _22;
    @SerializedName("23")
    @Expose
    public int _23;


    public int[] getRatings(){
        return new int[]{_0,_1,_2,_3,_4,_5,_6,_7,_8,_9,_10,_11,
                _12, _13,_14,_15,_16,_17,_18,_19,_20,_21,_22,_23};
    }
}
