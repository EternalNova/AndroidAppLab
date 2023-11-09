package com.example.lab1;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class n2wService {
    public static NumberToWordApi getApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://htmlweb.ru/json/convert/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(NumberToWordApi.class);
    }
}
