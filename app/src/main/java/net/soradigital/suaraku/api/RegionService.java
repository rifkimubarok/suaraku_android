package net.soradigital.suaraku.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RegionService {
    @Headers({
            "Accept: application/json",
    })

    @GET("region/province")
    Call<HashMap<String, Object>> province();

    @GET("region/city/{provinceId}")
    Call<HashMap<String, Object>> city(@Path("provinceId") String provinceId);
}
