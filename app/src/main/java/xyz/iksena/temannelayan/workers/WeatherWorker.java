package xyz.iksena.temannelayan.workers;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.iksena.temannelayan.models.Tidal;
import xyz.iksena.temannelayan.models.Weather;
import xyz.iksena.temannelayan.services.TidalService;
import xyz.iksena.temannelayan.services.WeatherService;

import static xyz.iksena.temannelayan.utils.DataUtils.KEY_LATITUDE;
import static xyz.iksena.temannelayan.utils.DataUtils.KEY_LONGITUDE;
import static xyz.iksena.temannelayan.utils.DataUtils.getLocation;

public class WeatherWorker extends Worker {

    private Context context;
    private Result result = Result.failure();

    public WeatherWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Call<Weather> call = WeatherService.getApi()
                .getWeather(getLocation(context).lat, getLocation(context).lon);
        call.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Weather weather = response.body();
                if (weather!=null){
                    if (weather.list!=null) {
                        Realm.getDefaultInstance().executeTransaction(t -> {
                            t.copyToRealm(weather);
                            //deleting previous data
                            RealmResults<Weather> weathers = t.where(Weather.class).sort("createdAt", Sort.DESCENDING).findAll();
                            if (weathers.size() > 1) {
                                int i = 0;
                                for (Weather item : weathers) {
                                    if (i > 0) item.deleteFromRealm();
                                    i++;
                                }
                            }
                        });
                        result = Result.success();
                    }
                } else {
                    Toast.makeText(context, "Gagal memuat data Cuaca", Toast.LENGTH_SHORT).show();
                    result = Result.failure();
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Toast.makeText(context, "Gagal memuat data Cuaca", Toast.LENGTH_SHORT).show();
                result = Result.failure();
            }
        });
        return result;
    }
}
