package xyz.iksena.temannelayan.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.room.Ignore;
import io.realm.RealmObject;

public class Coord extends RealmObject {

    @SerializedName("lat")
    @Expose
    public double lat;
    @SerializedName("lon")
    @Expose
    public double lon;

    public Coord() {
    }

    public Coord(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Ignore
    public LatLng getLatLng(){
        return new LatLng(lat, lon);
    }

    public static List<LatLng> getAllLatLng(List<Coord> coords){
        List<LatLng> latLngs = new ArrayList<>();
        for (Coord coord : coords)
            latLngs.add(new LatLng(coord.lat, coord.lon));
        return latLngs;
    }

    public static List<Coord> getAllCoords(List<LatLng> latLngs){
        List<Coord> coords = new ArrayList<>();
        for (LatLng latLng : latLngs)
            coords.add(new Coord(latLng.latitude, latLng.longitude));
        return coords;
    }
}
