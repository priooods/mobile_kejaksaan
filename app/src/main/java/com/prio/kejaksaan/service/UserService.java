package com.prio.kejaksaan.service;

import com.prio.kejaksaan.model.AtkItemModel;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.ModelLaporanATK;
import com.prio.kejaksaan.model.PembayaranModel;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.SuratModel;
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
    Call<UserModel> Login(
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
    Call<PerkaraListModel> AllPerkara();

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
    Call<AtkItemModel> AddATK(
            @Field("name") String name,
            @Query("keterangan") String keterangan,
            @Field("jumlah") Integer jumlah
    );
//tugas/jurusita
    @GET("atk/show")
    Call<AtkItemModel> AllAtk();

    @GET("perkara/proses/show")
    Call<PerkaraListModel> PerkaraSudahDiProsess();

    @FormUrlEncoded
    @POST("perkara/pp")
    Call<PerkaraListModel> PerkaraPP(
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
    @POST("perkara/proses/request")
    Call<List<PerkaraListModel.Proses>> PerkaraProsesPP(
            @Field("token") String token
    );

//    @FormUrlEncoded
    @GET("perkara/jurusita")
    Call<PerkaraListModel> AllJurusitaPerkara(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("tugas/jurusita")
    Call<PerkaraModel> TugasJurusita(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("tugas/jurusita/all")
    Call<SuratModel> AllJurusitaTugas(
            @Field("token") String token
    );
    @FormUrlEncoded
    @POST("tugas/all")
    Call<SuratModel> AllPanmudSurat(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("tugas/ppk")
    Call<SuratModel> TugasPPK(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("tugas/ppk/show")
    Call<SuratModel> AllPPkTugas(
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
    Call<SuratModel.Alone> AddJurusitaSurat(
            @Query("token") String token
            ,@Query("id") int id
            ,@Part MultipartBody.Part surat
    );

    @Multipart
    @POST("atk/req")
    Call<AtkModel> ReqATK(
            @Query("token") String token
            ,@Query("proses_id") int proses_id
            ,@PartMap Map<String, RequestBody> form
    );

    @FormUrlEncoded
    @POST("atk/pp")
    Call<AtkModel> ATkverifPP(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("atk/req/my")
    Call<AtkModel> ATkreqPP(
            @Field("token") String token
    );

    @GET("atk/all")
    Call<List<AtkItemModel.Item>> AtkList();


    @FormUrlEncoded
    @POST("atk/ppk/show")
    Call<AtkModel> ATkreqPPK(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("atk/ppk")
    Call<AtkModel> AtkNotifUntukPPK(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("atk/ppk/acc")
    Call<PerkaraModel> ReqATKPPK(
            @Field("token") String token
            , @Field("id") int id
    );

    @Multipart
    @POST("atk/log/acc")
    Call<AtkModel> VerifyATKLog(
            @Query("token") String token,
            @Query("id") int id
            ,@Part MultipartBody.Part penyerahan
    );

    @FormUrlEncoded
    @POST("atk/log")
    Call<AtkModel> ATkreqLog(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("atk/log/show")
    Call<AtkModel> ATkverifLog(
            @Field("token") String token
    );

    @GET("bayar/show")
    Call<PembayaranModel> PembyaranALLPPK();

    @FormUrlEncoded
    @POST("bayar/notif")
    Call<AtkModel> PembayaranAllBendahara(
            @Field("token") String token
    );

    @Multipart
    @POST("bayar/create")
    Call<PerkaraModel> BayarCreate(
            @Query("token") String token
            ,@Query("surat_id") Integer surat_id
            ,@Part MultipartBody.Part bayar
    );

    @Multipart
    @POST("bayar/acc")
    Call<PerkaraModel> BendaharaVerif(
            @Query("token") String token
            ,@Query("id") int id
            ,@Part MultipartBody.Part bayar
    );

    @GET("laporan/atk")
    Call<List<ModelLaporanATK>> LaporanAtkPPK();
}
