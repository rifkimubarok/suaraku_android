package net.soradigital.suaraku.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface SupporterService {
    @Headers({
            "Accept: application/json",
    })

    @GET("supporter/family")
    Call<HashMap<String, Object>> family(@Header("Authorization") String authorization);

    @DELETE("supporter/family/{noreg}/delete")
    Call<HashMap<String, Object>> deleteFamily(@Header("Authorization") String authorization, @Path("noreg") String noreg);

    @GET("supporter/invited")
    Call<HashMap<String, Object>> invited(@Header("Authorization") String authorization);
}
