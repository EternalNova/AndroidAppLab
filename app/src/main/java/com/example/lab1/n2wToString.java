package com.example.lab1;

import android.util.Log;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class n2wToString {
    public static void getWord(String number, final Consumer<String> callback){
        NumberToWordApi api = n2wService.getApi();
        Call<NumberToWord> call = api.getWord(number);
        call.enqueue(new Callback<NumberToWord>() {
            @Override
            public void onResponse(Call<NumberToWord> call, Response<NumberToWord> response) {
                NumberToWord n2w = response.body();
                String answer = null;
                if (n2w != null){
                    answer = n2w.str;
                }
                callback.accept(answer);
            }

            @Override
            public void onFailure(Call<NumberToWord> call, Throwable t) {
                Log.w("N2W", t.getMessage());
            }
        });
    }
}
