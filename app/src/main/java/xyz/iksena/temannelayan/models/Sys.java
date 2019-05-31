package xyz.iksena.temannelayan.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Sys extends RealmObject {
    @SerializedName("pod")
    @Expose
    public String pod;
}
