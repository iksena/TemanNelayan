package xyz.iksena.temannelayan.services;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import xyz.iksena.temannelayan.models.ApiKey;
import xyz.iksena.temannelayan.models.Tidal;

public class ApiKeyService {
    private static final String BASE_URL = "https://teman-nelayan-iksena.appspot.com/";

    public interface ApiKeyAPI {
        @GET("/")
        Call<List<ApiKey>> getApiKeys();
    }

    public static ApiKeyAPI getApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiKeyAPI.class);
    }
}
