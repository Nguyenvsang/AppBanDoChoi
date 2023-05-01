package com.example.appbandochoi.retrofit2;

import com.example.appbandochoi.constants.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {
    Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd HH:mm:ss").create();

    APIService apiService = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build().create(APIService.class);

    @FormUrlEncoded
    @POST("user/login")
    Call<ResponseBody> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("user/signup")
    Call<ResponseBody> signup(@Field("username") String username, @Field("password") String password,
                              @Field("firstname") String firstname, @Field("lastname") String lastname,
                              @Field("email") String email, @Field("phone") String phone);
}
