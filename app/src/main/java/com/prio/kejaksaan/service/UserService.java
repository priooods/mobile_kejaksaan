package com.prio.kejaksaan.service;

import com.prio.kejaksaan.model.UsersModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("login")
    Call<UsersModel> Login(
            @Field("name") String name,
            @Field("password") String password
    );
}
