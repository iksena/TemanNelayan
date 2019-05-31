package xyz.iksena.temannelayan.workers;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Response;
import xyz.iksena.temannelayan.models.Solunar;
import xyz.iksena.temannelayan.services.SolunarService;

import static xyz.iksena.temannelayan.utils.DataUtils.KEY_LATITUDE;
import static xyz.iksena.temannelayan.utils.DataUtils.KEY_LONGITUDE;

public class SolunarWorker extends Worker {

    private Context context;
    private Realm realm;

    public SolunarWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        //TODO replace default input data with saved coordinates
        double lat = getInputData().getDouble(KEY_LATITUDE, -8.750);
        double lon = getInputData().getDouble(KEY_LONGITUDE, 115.217);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Call<Solunar> call = SolunarService.getApi().getSolunar(lat, lon, sdf.format(new Date()));
        try {
            Response<Solunar> response = call.execute();
            Solunar solunar = response.body();
            if (solunar!=null){
                realm.executeTransaction(t->{
                    t.copyToRealmOrUpdate(solunar);
                    //deleting previous data
                    RealmResults<Solunar> solunars = realm.where(Solunar.class).sort("createdAt", Sort.DESCENDING).findAll();
                    if (solunars.size()>1){
                        int i = 0;
                        for (Solunar item : solunars){
                            if (i>0) item.deleteFromRealm();
                            i++;
                        }
                    }

                });
                return Result.success();
            } else {
                Toast.makeText(context, "Failed retrieving solunar data", Toast.LENGTH_SHORT).show();
                return Result.failure();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return Result.failure();
    }
}
