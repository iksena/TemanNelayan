package xyz.iksena.temannelayan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.iksena.temannelayan.R;
import xyz.iksena.temannelayan.models.Anchorage;
import xyz.iksena.temannelayan.models.ApiKey;
import xyz.iksena.temannelayan.models.Coord;
import xyz.iksena.temannelayan.services.ApiKeyService;
import xyz.iksena.temannelayan.workers.SolunarWorker;
import xyz.iksena.temannelayan.workers.TidalWorker;
import xyz.iksena.temannelayan.workers.WeatherWorker;

public class DataUtils {

    public static final String KEY_LATITUDE = "keyLat";
    public static final String KEY_LONGITUDE = "keyLon";
    public static final String KEY_LAST_UPDATED= "lastUpdateDate";
    public static final String TAG_UPDATE = "updateData";
    public static final String TAG_TIDAL = "updateTidalData";
    public static final String TAG_WEATHER = "updateWeatherData";
    public static final String TAG_SOLUNAR = "updateSolunarData";

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
    private static Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

    public static void saveLastUpdateTime(Context context, Date date){
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sPrefs.edit().putString(KEY_LAST_UPDATED, sdf.format(date)).apply();
    }

    public Date getLastUpdateTime(Context context) throws ParseException {
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String date = sPrefs.getString(KEY_LAST_UPDATED, sdf.format(new Date()));
        return sdf.parse(date);
    }

    public static void saveLocation(Context context, double lat, double lon){
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sPrefs.edit().putFloat(KEY_LATITUDE, (float) lat).apply();
        sPrefs.edit().putFloat(KEY_LONGITUDE, (float) lon).apply();
    }

    public static Coord getLocation(Context context){
        //TODO replace default input data with saved coordinates
        SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        double lat = sPrefs.getFloat(KEY_LATITUDE, -8.750f);
        double lon = sPrefs.getFloat(KEY_LONGITUDE, 115.217f);
        return new Coord(lat, lon);
    }

    public static boolean populateAnchorages(Context context, Realm realm){
        InputStream inRaw = context.getResources().openRawResource(R.raw.pelabuhan);
        Gson gson = new Gson();
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inRaw, "UTF-8"));
            List<Anchorage> anchorages = gson.fromJson(reader, new TypeToken<List<Anchorage>>(){}.getType());
            realm.executeTransaction(t-> realm.copyToRealmOrUpdate(anchorages));
            reader.close();
            inRaw.close();
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Data getInput(SharedPreferences sPrefs){
        double lat = sPrefs.getFloat(KEY_LATITUDE, 0f);
        double lon = sPrefs.getFloat(KEY_LONGITUDE, 0f);
        return new Data.Builder().putDouble(KEY_LATITUDE,lat).putDouble(KEY_LONGITUDE,lon).build();
    }

    public static OneTimeWorkRequest workWeather(SharedPreferences sPrefs){
        OneTimeWorkRequest reqWeather = new OneTimeWorkRequest.Builder(WeatherWorker.class)
                .setConstraints(constraints)
                .setInputData(getInput(sPrefs))
                .addTag(TAG_WEATHER)
                .addTag(TAG_UPDATE)
                .build();
        return reqWeather;
    }

    public static OneTimeWorkRequest workTidal(SharedPreferences sPrefs){
        OneTimeWorkRequest reqTidal = new OneTimeWorkRequest.Builder(TidalWorker.class)
                .setConstraints(constraints)
                .setInputData(getInput(sPrefs))
                .addTag(TAG_TIDAL)
                .addTag(TAG_UPDATE)
                .build();
        return reqTidal;
    }

    public static OneTimeWorkRequest workSolunar(SharedPreferences sPrefs){
        OneTimeWorkRequest reqSolunar = new OneTimeWorkRequest.Builder(SolunarWorker.class)
                .setConstraints(constraints)
                .setInputData(getInput(sPrefs))
                .addTag(TAG_SOLUNAR)
                .addTag(TAG_UPDATE)
                .build();
        return reqSolunar;
    }

    public static void enqueueAllWorks(SharedPreferences sPrefs, WorkManager workManager){
        workManager.beginWith(workWeather(sPrefs))
                .then(workTidal(sPrefs))
                .then(workSolunar(sPrefs))
                .enqueue();
    }

//    public static String getTidalApiKey(Realm realm){
//        ApiKey key = realm.where(ApiKey.class).contains("name","worldtides").findFirst();
//        if (key==null){
//            ApiKeyService.getApi().getApiKeys().enqueue(new Callback<List<ApiKey>>() {
//                @Override
//                public void onResponse(Call<List<ApiKey>> call, Response<List<ApiKey>> response) {
//                    List<ApiKey> apiKeys = response.body();
//                    if (apiKeys!=null) {
//                        realm.executeTransaction(t -> {
//                            t.copyToRealmOrUpdate(apiKeys);
//
//                        });
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<ApiKey>> call, Throwable t) {
//
//                }
//            });
//        } else {
//            apiKey = key.getKey();
//        }
//        return apiKey;
//    }

}

