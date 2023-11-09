package com.example.lab1;

import android.util.Log;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastToString {
    public static void getForecast(String city, final Consumer<String> callback){
        ForecastApi api = ForecastService.getApi();
        Call<Forecast> call = api.getCurrentWeather(city);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                Forecast result = response.body();
                if (result!=null) {
                    String answer = "сейчас в " + city + ' ' +
                            result.current.temperature +
                            " градуса " + " и " +
                            result.current.weather_descriptions.get(0);
                    callback.accept(answer);
                }
                else {
                    callback.accept("Не могу узнать погоду");
                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                Log.w("WEATHER", t.getMessage());
            }
        });
    }
}
