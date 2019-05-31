package xyz.iksena.temannelayan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Clouds extends RealmObject {
    @SerializedName("all")
    @Expose
    public int all;
}
