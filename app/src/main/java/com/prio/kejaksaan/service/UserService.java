package com.prio.kejaksaan.service;

import com.prio.kejaksaan.model.BaseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("login")
    Call<BaseModel> Login(
            @Field("name") String name,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("me")
    Call<BaseModel> UserDetail(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("logout")
    Call<BaseModel> Logout(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("register")
    Call<BaseModel> CreateUser(
            @Field("name") String name,
            @Field("fullname") String fullname,
            @Field("password") String password
            ,@Field("type") String type
    );
}
