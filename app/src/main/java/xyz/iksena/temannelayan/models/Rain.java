package xyz.iksena.temannelayan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Rain extends RealmObject {
    @SerializedName("3h")
    @Expose
    public double _3h;
}
