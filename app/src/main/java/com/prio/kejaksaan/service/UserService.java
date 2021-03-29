package com.prio.kejaksaan.service;

import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface UserService {

    @FormUrlEncoded
    @POST("login")
    Call<BaseModel> Login(
            @Field("name") String name,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("me")
    Call<UserModel> UserDetail(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("logout")
    Call<BaseModel> Logout(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("findall")
    Call<List<UserModel>> AllUsers(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("register")
    Call<UserModel> CreateUser(
            @Field("name") String name,
            @Field("fullname") String fullname,
            @Field("password") String password
            ,@Field("type") String type
    );

    @Multipart
    @POST("update")
    Call<BaseModel> updateUser(
            @Query("token") String token
            ,@PartMap Map<String, RequestBody> Users
            ,@Part MultipartBody.Part avatar
    );

    @GET("perkara/all")
    Call<List<PerkaraModel>> AllPerkara();

    @FormUrlEncoded
    @POST("perkara/create")
    Call<PerkaraModel> AddPerkara(
            @Field("tanggal") String tanggal,
            @Field("nomor") String nomor,
            @Field("jenis") String jenis
            ,@Field("identitas") String identitas
            ,@Field("dakwaan") String dakwaan
            ,@Field("penahanan") String penahanan
            ,@Field("pp") int pp
            ,@Field("jurusita") int jurusita
            ,@Field("token") String token
    );

    @FormUrlEncoded
    @POST("perkara/update")
    Call<PerkaraModel> UpdatePerkara(
            @Field("tanggal") String tanggal,
            @Field("nomor") String nomor,
            @Field("jenis") String jenis
            ,@Field("identitas") String identitas
            ,@Field("dakwaan") String dakwaan
            ,@Field("penahanan") String penahanan
            ,@Field("token") String token
            ,@Field("id") int id
    );

    @FormUrlEncoded
    @POST("perkara/delete")
    Call<BaseModel> DeletePerkara(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("atk/add")
    Call<AtkModel> AddATK(
            @Field("name") String name,
            @Field("keterangan") String keterangan,
            @Field("jumlah") int jumlah
    );
//tugas/jurusita
    @GET("atk/show")
    Call<List<AtkModel>> AllAtk();

    @GET("perkara/proses/show")
    Call<PerkaraModel> PerkaraSudahDiProsess();

    @FormUrlEncoded
    @POST("perkara/pp")
    Call<PerkaraModel> PerkaraPP(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("perkara/proses")
    Call<PerkaraModel> PerkaraAddProses(
            @Field("token") String token
            ,@Field("tanggal") String tanggal
            ,@Field("perkara_id") String perkara_id
            ,@Field("hari") String hari
            ,@Field("agenda") String agenda
    );

    @FormUrlEncoded
    @POST("perkara/jurusita")
    Call<PerkaraModel> AllJurusitaPerkara(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("tugas/jurusita")
    Call<PerkaraModel> TugasJurusita(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("tugas/jurusita/all")
    Call<DocumentModel> AllJurusitaTugas(
            @Field("token") String token
    );
    @FormUrlEncoded
    @POST("tugas/all")
    Call<DocumentModel> AllPanmudSurat(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("tugas/ppk")
    Call<PerkaraModel> TugasPPK(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("tugas/ppk/all")
    Call<DocumentModel> AllPPkTugas(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("tugas/acc")
    Call<PerkaraModel> VerifyPPK(
            @Field("token") String token,
            @Field("id") int id
    );

    @Multipart
    @POST("tugas/create")
    Call<DocumentModel> AddPanmudSurat(
            @Query("token") String token
            ,@Query("tipe") String tipe
            ,@Query("perkara_id") int perkara_id
            ,@Part MultipartBody.Part surat
    );

    @Multipart
    @POST("tugas/bukti")
    Call<DocumentModel> AddJurusitaSurat(
            @Query("token") String token
            ,@Query("id") int id
            ,@Part MultipartBody.Part surat
    );

    @Multipart
    @POST("atk/req")
    Call<AtkModel> ReqATK(
            @Query("token") String token
            ,@PartMap Map<Integer, RequestBody> form
    );
}
