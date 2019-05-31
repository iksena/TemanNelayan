package xyz.iksena.temannelayan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class DeviceUtils {

    private static final String TAG = DeviceUtils.class.getSimpleName();
    public static final int REQ_PERMISSION = 12;

    public static boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isGpsEnabled(Context context){
        PackageManager pm = context.getPackageManager();
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return pm != null
                && pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
                && lm != null
                && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isPermissionGranted(Context context, Activity activity, String... permissions){
        try {
            for (String s : permissions){
                if (ContextCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, permissions, REQ_PERMISSION);
                    return false;
                }
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * display a prompt window to enable gps instantly
     * (based on the priority of LocationRequest configuration object)
     * @param activity the activity where its called and its result to be processed (onActivityResult)
     * @param locationRequest configuration object
     * @param reqCode request code for onActivityResult
     */
    public static void promptEnableGps(Activity activity, LocationRequest locationRequest, int reqCode){
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    Log.i(TAG, "All location settings are satisfied.");
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i(TAG, "Location settings are not satisfied. " +
                            "Show the user a dialog to upgrade location settings ");
                    try {
                        status.startResolutionForResult(activity, reqCode);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i(TAG, "PendingIntent unable to execute request.");
                        activity.onBackPressed();
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    break;
            }
        });
    }
}
