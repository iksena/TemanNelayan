package xyz.iksena.temannelayan.services;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import xyz.iksena.temannelayan.models.Weather;

public class WeatherService {
    private static final String BASE_URL = "https://api.openweathermap.org";

    public interface WeatherAPI {
        @GET("/data/2.5/forecast?appId=9b3ba99b07215841121bae2e08fb7a7a&units=metric")
        Call<Weather> getWeather(@Query("lat") double lat,
                                 @Query("lon") double lon);
    }

    public static WeatherAPI getApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherAPI.class);
    }
}
