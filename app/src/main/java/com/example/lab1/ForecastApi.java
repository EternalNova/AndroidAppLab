package com.example.lab1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {
    @GET("/current?access_key=66767c86652e8000906c0b214acf5843")
    Call<Forecast> getCurrentWeather(@Query("query") String city);
}
