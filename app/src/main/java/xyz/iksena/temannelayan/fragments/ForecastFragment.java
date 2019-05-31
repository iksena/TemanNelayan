package xyz.iksena.temannelayan.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.iksena.temannelayan.R;
import xyz.iksena.temannelayan.adapters.TidesExtremeAdapter;
import xyz.iksena.temannelayan.models.ApiKey;
import xyz.iksena.temannelayan.models.City;
import xyz.iksena.temannelayan.models.Extreme;
import xyz.iksena.temannelayan.models.Height;
import xyz.iksena.temannelayan.models.Main;
import xyz.iksena.temannelayan.models.Tidal;
import xyz.iksena.temannelayan.models.Weather;
import xyz.iksena.temannelayan.models.WeatherData;
import xyz.iksena.temannelayan.models.WeatherDataList;
import xyz.iksena.temannelayan.models.Wind;
import xyz.iksena.temannelayan.services.ApiKeyService;
import xyz.iksena.temannelayan.services.TidalService;
import xyz.iksena.temannelayan.services.WeatherService;
import xyz.iksena.temannelayan.utils.DataUtils;
import xyz.iksena.temannelayan.utils.DeviceUtils;
import xyz.iksena.temannelayan.utils.WeatherUtils;

import static xyz.iksena.temannelayan.utils.DataUtils.getLocation;

public class ForecastFragment extends Fragment {

    @BindView(R.id.text_city)
    TextView textCity;
    @BindView(R.id.text_weather)
    TextView textWeather;
    @BindView(R.id.text_wind)
    TextView textWind;
    @BindView(R.id.text_pressure)
    TextView textPressure;
    @BindView(R.id.text_pressure_rate)
    TextView textPressureRate;
    @BindView(R.id.text_humidity)
    TextView textHumid;
    @BindView(R.id.text_temp)
    TextView textTemp;
    @BindView(R.id.chart_tides)
    LineChart chartTides;
    @BindView(R.id.rv_tides)
    RecyclerView rvTides;
    @BindView(R.id.btn_info_weather)
    Button btnInfoWeather;
    @BindView(R.id.btn_info_tides)
    Button btnInfoTides;
//    @BindView(R.id.chart_solunar)
//    LineChart chartSolunar;

    private SharedPreferences sPrefs;
    private Realm realm;
    private ForecastListener listener;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            (sharedPreferences, key) -> {
        if (key.equalsIgnoreCase(DataUtils.KEY_LATITUDE) || key.equalsIgnoreCase(DataUtils.KEY_LONGITUDE)){
            enqueueWeather(); enqueueTidal();
        }
    };
    private String apiKey;

    public ForecastFragment() {}

    public interface ForecastListener{
        void onInfoClick(int which);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ForecastListener) listener = (ForecastListener) context;
        sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        realm.addChangeListener(mRealm->setViews());
        sPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        realm.removeAllChangeListeners();
        sPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        ButterKnife.bind(this, view);
        setViews();
        return view;
    }

    private void setViews(){
        setWeatherViews();
        setTidalViews();
        if (listener!=null) {
            btnInfoWeather.setOnClickListener(v -> listener.onInfoClick(1));
            btnInfoTides.setOnClickListener(v -> listener.onInfoClick(0));
        }
    }

    private void setWeatherViews(){
        Calendar nowDate = Calendar.getInstance();
        Calendar dataDate = Calendar.getInstance();
        int nowDay = nowDate.get(Calendar.DAY_OF_YEAR);

        //WEATHER
        Weather weather = realm.where(Weather.class)
                .sort("createdAt", Sort.DESCENDING).findFirst();
        if (weather != null){
            dataDate.setTime(weather.createdAt);
            if (nowDay > dataDate.get(Calendar.DAY_OF_YEAR))
                enqueueWeather(); //get updated data from internet
            if (weather.list != null && !weather.list.isEmpty()) {
                WeatherDataList weatherDataList = weather.list.first(); //the data contains weather, clouds, wind, rain, city, etc
                for (WeatherDataList dataList : weather.list) { //checking if there's a forecast data saved
                    dataDate.setTimeInMillis(dataList.dt * 1000);
                    if (nowDay == dataDate.get(Calendar.DAY_OF_YEAR)) weatherDataList = dataList;
                }
                if (weatherDataList != null) {
                    dataDate.setTimeInMillis(weatherDataList.dt * 1000);
//                    if (nowDay > dataDate.get(Calendar.DAY_OF_YEAR)) {
//                        enqueueWeather(); //get updated data from internet
//                        return;
//                    }
                    City weatherCity = weather.city;
                    if (weatherCity!=null)
                        textCity.setText(getString(R.string.format_city, weatherCity.name, WeatherUtils.getDateFromSeconds(weatherDataList.dt, "dd MMM yyyy")));
                    WeatherData weatherData = weatherDataList.weather.first();
                    if (weatherData != null)
                        textWeather.setText(getString(R.string.today_weather, WeatherUtils.getStringForWeatherCondition(getContext(), weatherData.id)));
                    Wind weatherWind = weatherDataList.wind;
                    if (weatherWind != null)
                        textWind.setText(WeatherUtils.getFormattedWind(getContext(), weatherWind.speed, weatherWind.deg));
                    Main weatherMain = weatherDataList.main;
                    if (weatherMain != null) {
                        textPressure.setText(getString(R.string.format_pressure, weatherMain.seaLevel));
                        textPressureRate.setText(WeatherUtils.getPressureRate(weatherMain.seaLevel));
                        textPressureRate.setTextColor(getResources().getColor(WeatherUtils.getPressureRateColorRes(weatherMain.seaLevel)));
                        textTemp.setText(getString(R.string.format_temperature, weatherMain.temp));
                        textHumid.setText(getString(R.string.format_humidity, weatherMain.humidity));
                    }
                } else
                    enqueueWeather();
            } else
                enqueueWeather();
        } else
            enqueueWeather();
    }

    private void setTidalViews(){
        Calendar nowDate = Calendar.getInstance();
        Calendar dataDate = Calendar.getInstance();
        int nowDay = nowDate.get(Calendar.DAY_OF_YEAR);
        //TIDES
        Tidal tidal = realm.where(Tidal.class)
                .sort("createdAt", Sort.DESCENDING).findFirst();
        if (tidal!=null){
            dataDate.setTime(tidal.createdAt);
            if (nowDay > dataDate.get(Calendar.DAY_OF_YEAR))
                enqueueTidal();
            List<Height> heights = tidal.heights;
            if (heights != null && !heights.isEmpty()) {
                List<Entry> entries = new ArrayList<>();
                int i=0;
                int[] seconds = new int[heights.size()];
                for (Height height : heights) {
                    entries.add(new Entry((float) i, (float) height.height));
                    seconds[i] = height.dt;
                    i++;
                }
                LineDataSet heightDataSet = new LineDataSet(entries, "Tinggi Gelombang");
                chartTides.setData(new LineData(heightDataSet));
                //TOD O the date timezone isn't correct -- I think it's now correct
                XAxis xAxis = chartTides.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter((value, axis) -> {
                    int second = seconds[(int) value];
                    return WeatherUtils.getDateFromSeconds(second, "HH:mm'\n'dd/MM"); //it's safe coz it only queries the incremented index in a loop above
                });
                List<Extreme> extremes = new ArrayList<>(tidal.extremes);
                rvTides.setAdapter(new TidesExtremeAdapter(getContext(), extremes));
            } else
                enqueueTidal();
        } else
            enqueueTidal();
    }

    private void enqueueWeather(){
//        workManager.cancelAllWorkByTag(TAG_WEATHER);
//        workManager.enqueue(workWeather(sPrefs));
        Call<Weather> call = WeatherService.getApi()
                .getWeather(getLocation(getContext()).lat, getLocation(getContext()).lon);
        if (call.isExecuted()) call.cancel();
        if (getContext()!=null && DeviceUtils.isConnected(getContext())) {
            call.enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(Call<Weather> call, Response<Weather> response) {
                    Weather weather = response.body();
                    if (weather != null) {
                        if (weather.list != null) {
                            realm.executeTransaction(t -> {
                                t.copyToRealm(weather);
                                //deleting previous data
                                RealmResults<Weather> weathers = t.where(Weather.class)
                                        .sort("createdAt", Sort.DESCENDING).findAll();
                                if (weathers.size() > 1) {
                                    int i = 0;
                                    for (Weather item : weathers) {
                                        if (i > 0) item.deleteFromRealm();
                                        i++;
                                    }
                                }
                            });
                            setWeatherViews();
                        } else {
                            Toast.makeText(getContext(), "Gagal memuat data Cuaca", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Gagal memuat data Cuaca", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Weather> call, Throwable t) {
                    Toast.makeText(getContext(), "Gagal memuat data Cuaca", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void enqueueTidal(){
//        workManager.cancelAllWorkByTag(TAG_TIDAL);
//        workManager.enqueue(workTidal(sPrefs));
        ApiKey key = realm.where(ApiKey.class).contains("name","worldtides").findFirst();
        if (key==null){
            ApiKeyService.getApi().getApiKeys().enqueue(new Callback<List<ApiKey>>() {
                @Override
                public void onResponse(Call<List<ApiKey>> call, Response<List<ApiKey>> response) {
                    List<ApiKey> apiKeys = response.body();
                    if (apiKeys!=null) {
                        realm.executeTransaction(t -> {
                            t.copyToRealmOrUpdate(apiKeys);
                            apiKey = t.where(ApiKey.class).contains("name","worldtides")
                                    .findFirst().getKey();
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<ApiKey>> call, Throwable t) {

                }
            });
        } else {
            apiKey = key.getKey();
        }
        Call<Tidal> call = TidalService.getApi()
                .getTidal(getLocation(getContext()).lat, getLocation(getContext()).lon, apiKey);
        if (call.isExecuted()) call.cancel();
        if (getContext()!=null && DeviceUtils.isConnected(getContext())) {
            call.enqueue(new Callback<Tidal>() {
                @Override
                public void onResponse(Call<Tidal> call, Response<Tidal> response) {
                    Tidal tidal = response.body();
                    if (tidal != null) {
                        if (tidal.heights != null && tidal.extremes != null) {
                            realm.executeTransaction(t -> {
                                t.copyToRealm(tidal);
                                //deleting previous data
                                RealmResults<Tidal> tidals = t.where(Tidal.class)
                                        .sort("createdAt", Sort.DESCENDING).findAll();
                                if (tidals.size() > 1) {
                                    int i = 0;
                                    for (Tidal item : tidals) {
                                        if (i > 0) item.deleteFromRealm();
                                        i++;
                                    }
                                }
                                setTidalViews();
                            });
                        } else {
                            Toast.makeText(getContext(), "Gagal memuat data Pasang Surut. Geser ke bawah untuk muat ulang.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Gagal memuat data Pasang Surut. Geser ke bawah untuk muat ulang.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Tidal> call, Throwable t) {
                    Toast.makeText(getContext(), "Gagal memuat data Pasang Surut. Geser ke bawah untuk muat ulang.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
