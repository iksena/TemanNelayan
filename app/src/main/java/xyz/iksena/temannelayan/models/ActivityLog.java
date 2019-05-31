package xyz.iksena.temannelayan.models;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ActivityLog extends RealmObject {

    @PrimaryKey
    private String _id = UUID.randomUUID().toString();
    private Date createdAt = new Date();
    private int catches;
    private double routeDistance;
    private long duration;
    private RealmList<Coord> coords;
    private String message;

    public String get_id() {
        return _id;
    }

    public ActivityLog set_id(String _id) {
        this._id = _id;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ActivityLog setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public int getCatches() {
        return catches;
    }

    public ActivityLog setCatches(int catches) {
        this.catches = catches;
        return this;
    }

    public double getRouteDistance() {
        return routeDistance;
    }

    public ActivityLog setRouteDistance(double routeDistance) {
        this.routeDistance = routeDistance;
        return this;
    }

    public long getDuration() {
        return duration;
    }

    public ActivityLog setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public RealmList<Coord> getCoords() {
        return coords;
    }

    public ActivityLog setCoords(RealmList<Coord> coords) {
        this.coords = coords;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ActivityLog setMessage(String message) {
        this.message = message;
        return this;
    }

    public static double getDistance(List<Coord> coords){
        double sum = 0.0;
        for (int i=0; i<coords.size(); i++){
            Coord a = coords.get(i);
            Coord b = coords.get(i+1);
            if (b == null) break;
            double distance = Math.sqrt(Math.pow(b.lat-a.lat,2)+Math.pow(b.lon-a.lon,2));
            sum += distance;
        }
        return sum;
    }
}
