package com.prio.kejaksaan.service;

import com.prio.kejaksaan.model.AtkItemModel;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.AtkRequest;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.MessageModel;
import com.prio.kejaksaan.model.ModelLaporanATK;
import com.prio.kejaksaan.model.ModelNotification;
import com.prio.kejaksaan.model.PembayaranModel;
import com.prio.kejaksaan.model.PerkaraListModel;
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
    @POST("show")
    Call<List<UserModel>> FilterUser(
            @Field("token") String token,
            @Field("filter") String filter
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
    Call<MessageModel> AddPerkara(
            @Field("tanggal") String tanggal,
            @Field("nomor") String nomor,
            @Field("jenis") String jenis
            ,@Field("identitas") String identitas
            ,@Field("pp") int pp
            ,@Field("jurusita") int jurusita
            ,@Field("token") String token
    );

    @FormUrlEncoded
    @POST("perkara/update")
    Call<MessageModel> UpdatePerkara(
            @Field("tanggal") String tanggal,
            @Field("nomor") String nomor,
            @Field("jenis") String jenis
            ,@Field("identitas") String identitas
            ,@Field("token") String token
            ,@Field("id") int id
    );

    @FormUrlEncoded
    @POST("perkara/delete")
    Call<MessageModel> DeletePerkara(
            @Field("id") int id
    );

    @FormUrlEncoded
    @POST("atk/add")
    Call<MessageModel> AddATK(
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
    Call<MessageModel> PerkaraAddProses(
            @Field("token") String token
            ,@Field("tanggal") String tanggal
            ,@Field("perkara_id") String perkara_id
            ,@Field("hari") String hari
            ,@Field("agenda") String agenda
            ,@Field("dakwaan") String dakwaan
            ,@Field("penahanan") String penahanan
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
    Call<SuratModel> TugasJurusita(
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
    Call<MessageModel> VerifyPPK(
            @Field("token") String token,
            @Field("id") int id
    );

    @Multipart
    @POST("tugas/create")
    Call<SuratModel.Alone> AddPanmudSurat(
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
    Call<MessageModel> ReqATK(
            @Query("token") String token
            ,@Query("proses_id") int proses_id
            ,@PartMap Map<String, RequestBody> form
    );

    @FormUrlEncoded
    @POST("atk/pp")
    Call<AtkRequest> ATkverifPP(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("atk/req/my")
    Call<AtkRequest> ATkreqPP(
            @Field("token") String token
    );

    @GET("atk/all")
    Call<List<AtkItemModel.Item>> AtkList();


    @FormUrlEncoded
    @POST("atk/ppk/show")
    Call<AtkRequest> ATkreqPPK(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("atk/ppk")
    Call<AtkRequest> ATkverifPPK(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("atk/ppk/acc")
    Call<MessageModel> AtkaccPPK(
            @Field("token") String token
            , @Field("id") int id
    );

    @Multipart
    @POST("atk/pp/acc")
    Call<MessageModel> AtkaccPP(
            @Query("token") String token
            , @Query("id") int id
            ,@Part MultipartBody.Part penerimaan
    );

    @Multipart
    @POST("atk/log/acc")
    Call<MessageModel> VerifyATKLog(
            @Query("token") String token,
            @Query("id") int id
            ,@Part MultipartBody.Part penyerahan
    );

    @FormUrlEncoded
    @POST("atk/log/show")
    Call<AtkRequest> ATkreqLog(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("atk/log")
    Call<AtkRequest> ATkverifLog(
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
    Call<MessageModel> BayarCreate(
            @Query("token") String token
            ,@Query("surat_id") Integer surat_id
            ,@Part MultipartBody.Part bayar
    );

    @Multipart
    @POST("bayar/acc")
    Call<PembayaranModel.Alone> BendaharaVerif(
            @Query("token") String token
            ,@Query("id") int id
            ,@Part MultipartBody.Part bayar
    );

    @GET("laporan/atk")
    Call<List<ModelLaporanATK>> LaporanAtkPPK();

    @FormUrlEncoded
    @POST("notif2")
    Call<ModelNotification> AllNotifikasiUser(
            @Field("token") String token
    );
}
