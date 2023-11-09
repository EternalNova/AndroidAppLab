package com.example.lab1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NumberToWordApi {
    @GET("num2str?api_key=837270034e6332353713c8d806db5cbb")
    Call<NumberToWord> getWord(@Query("num") String number);
}
