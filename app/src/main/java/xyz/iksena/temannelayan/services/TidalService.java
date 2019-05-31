package xyz.iksena.temannelayan.services;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import xyz.iksena.temannelayan.models.Tidal;

public class TidalService {
    private static final String BASE_URL = "https://www.worldtides.info/";

    public interface TidalAPI {
        //GET: https://www.worldtides.info/api?extremes&heights&step=3600&key=612ee4a1-9b0a-476a-9454-a9b09a034342&lat=-8.750&lon=115.217
        @GET("/api?extremes&heights&step=3600&key=90e58aa2-996b-4ecd-ac84-4a14d63ff984")
        Call<Tidal> getTidal(@Query("lat") double lat,
                             @Query("lon") double lon,
                             @Query("key") String key);
    }

    public static TidalAPI getApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TidalAPI.class);
    }
}
