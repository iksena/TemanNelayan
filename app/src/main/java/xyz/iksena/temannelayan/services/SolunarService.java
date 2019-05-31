package xyz.iksena.temannelayan.services;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import xyz.iksena.temannelayan.models.Solunar;

public class SolunarService {

    private static final String BASE_URL = "https://api.solunar.org/";

    public interface SolunarAPI {
        @GET("/solunar/{lat},{lon},{date},+7")
        Call<Solunar> getSolunar(@Path("lat") double lat,
                                 @Path("lon") double lon,
                                 @Path("date") String date);
    }

    public static SolunarAPI getApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(SolunarAPI.class);
    }
}
