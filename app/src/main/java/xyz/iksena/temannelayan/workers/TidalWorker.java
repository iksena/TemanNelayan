package xyz.iksena.temannelayan.workers;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.iksena.temannelayan.models.ApiKey;
import xyz.iksena.temannelayan.models.Tidal;
import xyz.iksena.temannelayan.services.ApiKeyService;
import xyz.iksena.temannelayan.services.TidalService;

import static xyz.iksena.temannelayan.utils.DataUtils.KEY_LATITUDE;
import static xyz.iksena.temannelayan.utils.DataUtils.KEY_LONGITUDE;
import static xyz.iksena.temannelayan.utils.DataUtils.getLocation;

public class TidalWorker extends Worker {

    private Context context;
    private Result result = Result.failure();
    private String apiKey;
    private Realm realm;

    public TidalWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
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
                .getTidal(getLocation(context).lat, getLocation(context).lon,apiKey);
        call.enqueue(new Callback<Tidal>() {
            @Override
            public void onResponse(Call<Tidal> call, Response<Tidal> response) {
                Tidal tidal = response.body();
                if (tidal != null){
                    if (tidal.heights!=null && tidal.extremes!=null) {
                        Realm.getDefaultInstance().executeTransaction(t -> {
                            t.copyToRealm(tidal);
                            //deleting previous data
                            RealmResults<Tidal> tidals = t.where(Tidal.class).sort("createdAt", Sort.DESCENDING).findAll();
                            if (tidals.size() > 1) {
                                int i = 0;
                                for (Tidal item : tidals) {
                                    if (i > 0) item.deleteFromRealm();
                                    i++;
                                }
                            }

                        });
                        result = Result.success();
                    } else {
                        result = Result.failure();
                        Toast.makeText(context, "Gagal memuat data Pasang Surut", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    result = Result.failure();
                    Toast.makeText(context, "Gagal memuat data Pasang Surut", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tidal> call, Throwable t) {
                result = Result.failure();
                Toast.makeText(context, "Gagal memuat data Pasang Surut", Toast.LENGTH_SHORT).show();
            }
        });
        return result;
    }
}
