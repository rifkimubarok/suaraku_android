package net.soradigital.suaraku.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CandidateService {
    @Headers({
            "Accept: application/json",
    })

    @GET("candidate/{cityId}")
    Call<HashMap<String, Object>> getCandidate(@Path("cityId") String cityId);

    @GET("candidate/")
    Call<HashMap<String, Object>> getCandidate2(@Header("Authorization") String authorization);
}
