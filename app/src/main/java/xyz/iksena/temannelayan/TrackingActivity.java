package xyz.iksena.temannelayan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import xyz.iksena.temannelayan.models.ActivityLog;
import xyz.iksena.temannelayan.models.Coord;
import xyz.iksena.temannelayan.utils.DataUtils;
import xyz.iksena.temannelayan.utils.DeviceUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.maps.android.data.MultiGeometry;
import com.google.maps.android.data.kml.KmlContainer;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static xyz.iksena.temannelayan.utils.DataUtils.getLocation;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    //    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    @BindView(R.id.edit_message)
    EditText editMessage;
    @BindView(R.id.edit_catches)
    EditText editCatches;
    @BindView(R.id.text_duration)
    TextView textDuration;
    @BindView(R.id.text_distance)
    TextView textDistance;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindString(R.string.btn_start)
    String strStart;
    @BindString(R.string.btn_stop)
    String strStop;

    private static final int REQ_PROMPT_GPS = 11;
    private static final String TAG = TrackingActivity.class.getSimpleName();
    private static final String ROUTE_TAG = "fishingRoute";
    private FusedLocationProviderClient locationProviderClient;
    private Polyline route;
    private GoogleMap googleMap;
    private List<Coord> coords;
    private boolean isRunning = false;
    private Realm realm;
    private double distance = 0.00;
    private long duration = 0;
    private Date durationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar!=null) {
//            actionBar.setTitle("Aktivitas Penangkapan Ikan");
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        textDistance.setText(getString(R.string.format_distance, distance));
        textDuration.setText(getString(R.string.format_duration, 0, 0));
        btnStart.setOnClickListener(v -> {
            if (!isRunning)
                updateLocation();
            else
                stopTracking();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRunning", isRunning);
        outState.putDouble("distance", distance);
        outState.putLong("duration", duration);
        outState.putLong("durationTime", durationTime.getTime());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState!=null){
            isRunning = savedInstanceState.getBoolean("isRunning", false);
            if (isRunning) {
                distance = savedInstanceState.getDouble("distance", 0);
                duration = savedInstanceState.getLong("duration", 0);
                durationTime = new Date(savedInstanceState.getLong("durationTime",
                        new Date().getTime()));
                updateLocation();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTracking();
    }

    private void stopTracking() {
        if (isRunning) {
            locationProviderClient.removeLocationUpdates(locationCallback);
            String message = editMessage.getText().toString();
            String catches = editCatches.getText().toString();
            realm.executeTransaction(t -> {
                ActivityLog log = new ActivityLog();
                log.setMessage(message);
                log.setCatches(!catches.equalsIgnoreCase("") ? Integer.valueOf(catches) : 0);
                log.setDuration(duration);
                log.setRouteDistance(distance);
                log.setCoords((RealmList<Coord>) coords);
                t.copyToRealmOrUpdate(log);
            });
            btnStart.setText(strStart);
            isRunning = false;
            distance = 0.00;
            duration = 0;
            finish();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        try {
            KmlLayer layer = new KmlLayer(googleMap, R.raw.zee, this);
            layer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PolylineOptions routeLines = new PolylineOptions();
        if (coords != null && !coords.isEmpty())
            routeLines.addAll(Coord.getAllLatLng(coords));
        else
            coords = new RealmList<>();
        route = googleMap.addPolyline(routeLines);
        route.setTag(ROUTE_TAG);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getLocation(this).lat, getLocation(this).lon), 10));
        if (DeviceUtils.isPermissionGranted(this, this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
            googleMap.setMyLocationEnabled(true);
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                DataUtils.saveLocation(TrackingActivity.this, lat, lon);
                Coord newCoord = new Coord(lat, lon);
                coords.add(newCoord);
                if (googleMap!=null) {
//                    googleMap.addMarker(new MarkerOptions()
//                            .flat(true)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_foreground))
//                            .anchor(0.5f, 0.5f)
//                            .position(newCoord.getLatLng()));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(newCoord.getLatLng()));
                }
                if (route!=null) {
                    List<LatLng> points = route.getPoints();
                    points.add(newCoord.getLatLng());
                    route.setPoints(points);
                    LatLng last = points.get(points.size()-1);
                    if (last!=null){
                        //distance
                        float[] results = new float[3];
                        Location.distanceBetween(last.latitude, last.longitude, lat, lon, results);
                        distance += results[0];
                        textDistance.setText(getString(R.string.format_distance, distance));
                    }
                }
                //duration
                Date now = new Date();
                duration += now.getTime() - durationTime.getTime();
                durationTime = now;
                textDuration.setText(getString(R.string.format_duration,
                        TimeUnit.MILLISECONDS.toHours(duration),
                        TimeUnit.MILLISECONDS.toMinutes(duration) -
                        TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(duration))));
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void updateLocation() {
        btnStart.setText(strStop);
        isRunning = true;
        if (durationTime == null) durationTime = new Date();
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(2 * 60 * 1000); //every 2 minutes
        locationRequest.setFastestInterval(60 * 1000);
        locationRequest.setMaxWaitTime(10 * 60 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (!DeviceUtils.isGpsEnabled(this)) {
            DeviceUtils.promptEnableGps(this, locationRequest, REQ_PROMPT_GPS);
            Toast.makeText(this, "Mohon nyalakan GPS jika tersedia", Toast.LENGTH_SHORT).show();
        } else {
            if (DeviceUtils.isPermissionGranted(this, this,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else
                Toast.makeText(this, "Mohon ijinkan aplikasi untuk mengambil lokasi", Toast.LENGTH_SHORT).show();
        }
    }

    private List<KmlPolygon> getPolygons(Iterable<KmlContainer> containers) {
        List<KmlPolygon> polygons = new ArrayList<>();
        if (containers != null) {
            KmlContainer container = containers.iterator().next(); //karena cuma 1 (setelah didebug)
            KmlContainer container1 = container.getContainers().iterator().next(); //ambil yang dalemnya lagi
            for (KmlPlacemark placemark : container1.getPlacemarks()) { //baru ketemu dah
                MultiGeometry multiGeometry = (MultiGeometry) placemark.getGeometry();
                KmlPolygon kmlPolygon = (KmlPolygon) multiGeometry.getGeometryObject().get(0);
                polygons.add(kmlPolygon);
            }
        }
        return polygons;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PROMPT_GPS) {
            if (resultCode == RESULT_OK) {
                updateLocation();
            }else {
                stopTracking();
                finish();
            }
        }
    }
}
