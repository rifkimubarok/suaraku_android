package net.soradigital.suaraku.api;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {
    @Headers({
            "Accept: application/json",
    })

    @FormUrlEncoded
    @POST("login")
    Call<HashMap<String, Object>> login(@Field("username") String username,
                                        @Field("password") String password);

    @FormUrlEncoded
    @POST("register")
    Call<HashMap<String, Object>> register(@Field("phone_number") String phone_number,
                                           @Field("reff_id") String reff_id,
                                           @Field("acc_kowil") String acc_kowil,
                                           @Field("pem_code") String pem_code,
                                           @Field("username") String username,
                                           @Field("password") String password,
                                           @Field("name") String name);

    @GET("mobile/request-token")
    Call<HashMap<String, Object>> requestToken(@Header("Authorization") String authorization);
}
