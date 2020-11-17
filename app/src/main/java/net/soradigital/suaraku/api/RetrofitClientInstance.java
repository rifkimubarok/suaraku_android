package net.soradigital.suaraku.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
    private static Retrofit retrofit;
    // end point api yang digunakan
//    private static final String BASE_URL = "https://api.suaraku.sieradigital.com/api/v1/";
    private static final String BASE_URL = "http://192.168.141.102:8000/api/v1/";

    // pembuatan instance retrofit
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}