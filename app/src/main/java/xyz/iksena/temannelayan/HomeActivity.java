package xyz.iksena.temannelayan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.iksena.temannelayan.fragments.ActivityFragment;
import xyz.iksena.temannelayan.fragments.ExploreFragment;
import xyz.iksena.temannelayan.fragments.ForecastFragment;
import xyz.iksena.temannelayan.fragments.InfoFragment;
import xyz.iksena.temannelayan.models.ActivityLog;
import xyz.iksena.temannelayan.models.ApiKey;
import xyz.iksena.temannelayan.services.ApiKeyService;
import xyz.iksena.temannelayan.utils.DataUtils;
import xyz.iksena.temannelayan.utils.DeviceUtils;

import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        ActivityFragment.OnListFragmentInteractionListener,
        ForecastFragment.ForecastListener {
    private static final int REQ_PROMPT_GPS = 11;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.container)
    FrameLayout container;

    private Realm realm;
    private FusedLocationProviderClient locationProviderClient;
    private int whichInfo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        DeviceUtils.isPermissionGranted(this,this, Manifest.permission_group.STORAGE);
        //first time
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstTime = sPrefs.getBoolean("firstTime", true);
        if (isFirstTime) {
            DataUtils.populateAnchorages(this, realm);
            sPrefs.edit().putBoolean("firstTime", false).apply();
            ApiKeyService.getApi().getApiKeys().enqueue(new Callback<List<ApiKey>>() {
                @Override
                public void onResponse(Call<List<ApiKey>> call, Response<List<ApiKey>> response) {
                    List<ApiKey> apiKeys = response.body();
                    if (apiKeys!=null) {
                        realm.executeTransaction(t -> {
                            t.copyToRealmOrUpdate(apiKeys);
                        });
                    }
                }
                @Override
                public void onFailure(Call<List<ApiKey>> call, Throwable t) {}
            });
        }
        setViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocation();
    }

    private void setViews() {
        navigation.setOnNavigationItemSelectedListener(this);
        navigation.setSelectedItemId(R.id.navigation_forecast);
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null)
                DataUtils.saveLocation(HomeActivity.this,
                        location.getLatitude(), location.getLongitude());
        }
    };

    @SuppressLint("MissingPermission")
    private void updateLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setNumUpdates(1); //single update
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

    @Override
    protected void onPause() {
        super.onPause();
        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.navigation_forecast:
                fragment = new ForecastFragment();
                break;
            case R.id.navigation_explore:
                fragment = new ExploreFragment();
                break;
            case R.id.navigation_activity:
                fragment = new ActivityFragment();
                break;
            case R.id.navigation_info:
                fragment = new InfoFragment();
                Bundle args = new Bundle();
                args.putInt("whichInfo", whichInfo);
                fragment.setArguments(args);
                break;
        }
        if (fragment!=null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PROMPT_GPS) {
            if (resultCode == RESULT_OK)
                updateLocation();
        }
    }

    @Override
    public void onListFragmentInteraction(ActivityLog item) {
        if (item==null)
            startActivity(new Intent(this, TrackingActivity.class));
        else
            realm.executeTransaction(t->item.deleteFromRealm());
    }

    @Override
    public void onInfoClick(int which) {
        whichInfo = which;
        navigation.setSelectedItemId(R.id.navigation_info);
    }
}
