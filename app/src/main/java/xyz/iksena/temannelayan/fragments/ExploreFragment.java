package xyz.iksena.temannelayan.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import xyz.iksena.temannelayan.R;
import xyz.iksena.temannelayan.models.Anchorage;
import xyz.iksena.temannelayan.utils.DataUtils;
import xyz.iksena.temannelayan.utils.DeviceUtils;

import static xyz.iksena.temannelayan.utils.DataUtils.getLocation;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment implements OnMapReadyCallback {

    public ExploreFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.bind(this, view);
//        if (getActivity()!=null) {
//            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//            if (actionBar!=null) actionBar.setTitle("Pelabuhan Perikanan");
//        }
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        RealmResults<Anchorage> anchorages = Realm.getDefaultInstance().where(Anchorage.class).findAll();
        for (Anchorage a : anchorages){
            LatLng location = new LatLng(a.lat, a.lon);
            googleMap.addMarker(new MarkerOptions().position(location).title(a.label));
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getLocation(getContext()).lat,getLocation(getContext()).lon), 10));
        if (DeviceUtils.isPermissionGranted(getContext(), getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
            googleMap.setMyLocationEnabled(true);

    }
}
