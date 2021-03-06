package com.prio.kejaksaan.views.document;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.DialogAddSuratBinding;
import com.prio.kejaksaan.layer.Layer_Anggaran;
import com.prio.kejaksaan.layer.Layer_Document;
import com.prio.kejaksaan.layer.Layer_Perkara;
import com.prio.kejaksaan.layer.Layer_Persediaan;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.MessageModel;
import com.prio.kejaksaan.model.PembayaranModel;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.SuratModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.ConvertFiles;
import com.prio.kejaksaan.tools.RealPathUtil;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
public class AddDocument extends DialogFragment {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity){
        // Check if we have write permission
        int permission=ActivityCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permission!=PackageManager.PERMISSION_GRANTED){
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullscreen);
    }

    String[] listType = {"Pengiriman Penetapan Hari Sidang","Pengiriman Petikan / Salinan Putusan",
            "Pengiriman Surat Penahanan & Perpanjangan Penahanan","Pengiriman Salinan Putusan",
            "Pemberitahuan Proses Banding","Pemberitahuan Putusan Banding","Pemberitahuan Proses Kasasi","Pemberitahuan Putusan Kasasi"};
    DialogAddSuratBinding binding;
    public static int REQUEST_SETTING = 168;
    public static int PRIVATE_CODE = 1;
    public int mode;
    SuratModel.Item surat;
    PerkaraListModel.Item perkara;
    PembayaranModel.Item bayar;
    Intent openFileManager;
    File files;

    public AddDocument(int mode){
        this.mode = mode;
    }
    public AddDocument(int mode, @Nullable SuratModel.Item model){
        this.mode = mode;
        this.surat = model;
    }
    public AddDocument(int mode, PerkaraListModel.Item model){
        this.mode = mode;
        this.perkara = model;
    }
    public AddDocument(int mode, PembayaranModel.Item model){
        this.mode = mode;
        this.bayar = model;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddSuratBinding.inflate(inflater, container, false);
        binding.back.setOnClickListener(v -> dismiss());
//        PerkaraListModel.Item perkara;
        switch (mode){
            case 1: //ini untuk panmud upload files
                binding.nama.setText(perkara.identitas);
                binding.l2.setVisibility(View.GONE);
//                binding.dakwaan.setText(perkara.proses.dakwaan);
                binding.nomor.setText(perkara.nomor);
                binding.jenisPerkara.setText(perkara.jenis);
                binding.tanggal.setText(perkara.tanggal);
                binding.penahanan.setText(perkara.proses.penahanan);
                binding.ppName.setText(perkara.fullname_pp);
                binding.jurusitaName.setText(perkara.fullname_jurusita);
                binding.surat.setVisibility(View.GONE);
                binding.l10.setVisibility(View.GONE);
                binding.uploadFile.setVisibility(View.VISIBLE);
                binding.iconCameraUpload.setVisibility(View.VISIBLE);
                binding.titleLayout.setVisibility(View.VISIBLE);
                binding.title.clearFocus();
                binding.btnCreateletter.setOnClickListener(v -> {
                    if (Objects.requireNonNull(binding.title.getText()).toString().isEmpty() || files == null){
                        MDToast.makeText(requireContext(), "Harap isi semua masukan yang diperlukan!", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    } else {
                        binding.progress.setVisibility(View.VISIBLE);
                        UploadSuratPanmud();
                    }
                });
                break;
            case 2: //ini untuk panmud detail sudah ada files di upload
                binding.top.setText("Surat Tugas");
                binding.top2.setText("Tekan tombol unduh untuk melihat surat");
                binding.l1.setVisibility(View.GONE);
                binding.btnCreateletter.setVisibility(View.GONE);
                perkara = surat.perkara;
                binding.nama.setText(perkara.identitas);
                binding.dakwaan.setVisibility(View.GONE);
//                binding.dakwaan.setText(perkara.proses.dakwaan);
                binding.nomor.setText(perkara.nomor);
                binding.jenisPerkara.setText(perkara.jenis);
                binding.tanggal.setText(perkara.tanggal);
                binding.penahanan.setText(perkara.proses.penahanan);
                binding.ppName.setText(perkara.fullname_pp);
                binding.jurusitaName.setText(perkara.fullname_jurusita);
                binding.surat.setVisibility(View.VISIBLE);
                if (surat.daftar_pengantar == null){
                    binding.btnShowb.setVisibility(View.INVISIBLE);
                    binding.btnShowb.setOnClickListener(v -> {
                        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+surat.daftar_pengantar));
                        startActivity(web);
                    });
                }
                if (surat.verifier_id !=null){
                    binding.l11.setVisibility(View.VISIBLE);
                    binding.pemverifikasi.setText(surat.fullname_ppk);
                }

                binding.btnShowa.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+surat.surat_tugas));
                    startActivity(web);
                });
                break;
            case 3: // ini untuk jurusita upload files
                binding.top.setText("Surat Tugas");
                binding.top2.setText("Pilih file untuk mengirimkan bukti pengantar (bukti perjalanan dinas)");
                perkara = surat.perkara;
                binding.nama.setText(perkara.identitas);
                binding.dakwaan.setVisibility(View.GONE);
//                binding.dakwaan.setText(perkara.proses.dakwaan);
                binding.nomor.setText(perkara.nomor);
                binding.jenisPerkara.setText(perkara.jenis);
                binding.tanggal.setText(perkara.tanggal);
                binding.titleLayout.setVisibility(View.GONE);
                binding.penahanan.setText(perkara.proses.penahanan);
                binding.l7.setVisibility(View.GONE);
                binding.uploadFile.setVisibility(View.VISIBLE);
                binding.l8.setVisibility(View.GONE);
                binding.iconCameraUpload.setVisibility(View.VISIBLE);
                binding.btnCreateletter.setOnClickListener(v -> {
                    if (files == null){
                        MDToast.makeText(requireContext(), "Please Selected files", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    } else {
                        binding.progress.setVisibility(View.VISIBLE);
                        UploadSuratJurusita();
                    }
                });
                binding.surat.setVisibility(View.VISIBLE);
                binding.btnShowa.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+surat.surat_tugas));
                    startActivity(web);
                });
                if (surat.daftar_pengantar == null) {
                    binding.btnShowb.setVisibility(View.INVISIBLE);
                }else{
                    binding.btnShowb.setOnClickListener(v -> {
                        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+surat.daftar_pengantar));
                        startActivity(web);
                    });

                    binding.uploadFile.setVisibility(View.GONE);
                    binding.btnCreateletter.setVisibility(View.GONE);
                }
                if (surat.verifier_id !=null){
                    binding.l11.setVisibility(View.VISIBLE);
                    binding.pemverifikasi.setText(surat.fullname_ppk);
                }
                break;
            case 5:
                binding.l11.setVisibility(View.VISIBLE);
                binding.pemverifikasi.setText(surat.fullname_ppk);
                binding.btnCreateletter.setVisibility(View.GONE);
                if (surat.pembayaran==null){
                    binding.btnBayar.setVisibility(View.VISIBLE);
                    binding.l11.setVisibility(View.VISIBLE);
                    binding.uploadFile.setOnClickListener(v -> Permission());
                    binding.uploadFile.setVisibility(View.VISIBLE);
                    binding.iconCameraUpload.setVisibility(View.VISIBLE);
                    binding.btnBayar.setOnClickListener(v -> {
                        if (files == null){
                            MDToast.makeText(requireContext(), "Pilih file permintaan bayar", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                        } else {
                            binding.progress.setVisibility(View.VISIBLE);
                            MintaBayar();
                        }
                    });
                }else{
                    binding.l1.setVisibility(View.GONE);
                }
            case 4:
                binding.top.setText("Surat Tugas");
                binding.top2.setText("Tekan tombol unduh untuk melihat surat");
                perkara = surat.perkara;
                binding.a.setText(surat.tipe);
                binding.nama.setText(perkara.identitas);
                binding.dakwaan.setVisibility(View.GONE);
                binding.bantuanBiaya.setVisibility(View.VISIBLE);
                binding.l2.setVisibility(View.GONE);
                binding.titleBiaya.setText("Rp."+surat.biaya+",-");
                binding.nomor.setText(perkara.nomor);
                binding.jenisPerkara.setText(perkara.jenis);
                binding.tanggal.setText(perkara.tanggal);
                binding.penahanan.setText(perkara.proses.penahanan);
                binding.l10.setVisibility(View.VISIBLE);

                binding.titleLayout.setVisibility(View.GONE);
                binding.ppName.setText(perkara.fullname_pp);
                binding.jurusitaName.setText(perkara.fullname_jurusita);
                binding.surat.setVisibility(View.VISIBLE);

                if (surat.daftar_pengantar == null){
                    binding.btnShowb.setVisibility(View.INVISIBLE);
                    binding.btnCreateletter.setVisibility(View.GONE);
                }else{
                    binding.btnShowb.setOnClickListener(v -> {
                        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+surat.daftar_pengantar));
                        startActivity(web);
                    });
                    binding.btnCreateletter.setOnClickListener(v -> VerifyPPK());
                    binding.btnCreateletter.setText("Verifikasi!");
                }
                binding.btnShowa.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+surat.surat_tugas));
                    startActivity(web);
                });
                break;
            case 7:
                if (bayar.kuitansi == null) {
                    binding.uploadFile.setVisibility(View.VISIBLE);
                    binding.btnCreateletter.setText("Kirim Kwitansi");
                    binding.iconCameraUpload.setVisibility(View.VISIBLE);
                    binding.btnCreateletter.setOnClickListener(v -> {
                        if (files == null) {
                            MDToast.makeText(requireContext(), "Please Completely all forms", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                        } else {
                            binding.progress.setVisibility(View.VISIBLE);
                            VerifikasiBendahara();
                        }
                    });
                }else{
                    binding.btnCreateletter.setVisibility(View.GONE);
                }
            case 6:
                if (mode == 6) {
                    binding.top2.setText("Tekan tombol unduh untuk melihat surat!");
                    binding.btnCreateletter.setVisibility(View.GONE);
                }else
                    binding.top2.setText("Unggah berkas PDF/JPG/PNG Kuitansi untuk verifikasi permintaan pembiayaan");
                //surat kwitansi ada
//                binding.btnUpreq.setVisibility(View.GONE);
                binding.surat.setVisibility(View.VISIBLE);
                if (bayar.kuitansi == null)
                    binding.btnUpkwit.setVisibility(View.INVISIBLE);
                else{
                    binding.btnUpkwit.setOnClickListener(v -> {
                        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+bayar.kuitansi));
                        startActivity(web);
                    });
                }
                binding.btnUpreq.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+bayar.surat));
                    startActivity(web);
                });
                binding.btnShowa.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+bayar.surat_tugas.surat_tugas));
                    startActivity(web);
                });
                binding.btnShowb.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+bayar.surat_tugas.daftar_pengantar));
                    startActivity(web);
                });

                binding.ppkuploadsurat.setVisibility(View.VISIBLE);
                binding.l11.setVisibility(View.VISIBLE);
                binding.l7.setVisibility(View.GONE);
                binding.titleLayout.setVisibility(View.GONE);
                binding.top.setText("Permintaan Pembayaran");
                binding.a.setText("Data Surat Tugas");

                binding.namat.setText("Tipe Surat");
                binding.nama.setText(bayar.surat_tugas.tipe);
                binding.dakwaant.setText("Waktu Input Pengantar");
                binding.dakwaan.setText(bayar.surat_tugas.daftar_time);

                binding.nomor.setText(bayar.surat_tugas.perkara.nomor);
                binding.jenisPerkarat.setText("Jenis Perkara");
                binding.jenisPerkara.setText(bayar.surat_tugas.perkara.jenis);
                binding.tanggalt.setText("Tgl. Proses Perkara");
                binding.tanggal.setText(bayar.surat_tugas.perkara.proses.tanggal);
                binding.penahanant.setText("Agenda");
                binding.penahanan.setText(bayar.surat_tugas.perkara.proses.agenda);

                binding.jurusitaName.setText(bayar.surat_tugas.perkara.fullname_jurusita);
                binding.pemverifikasi.setText(bayar.fullname_ppk);
                break;
        }

        binding.title.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, listType);
        binding.title.setAdapter(adapter);
        binding.uploadFile.setOnClickListener(v -> Permission());
        binding.iconCameraUpload.setOnClickListener(v-> uploadCameras());
        binding.deleteFile.setOnClickListener(v -> {
            binding.uploadFile.setVisibility(View.VISIBLE);
            binding.iconCameraUpload.setVisibility(View.VISIBLE);
            files = null;
            binding.nameFile.setText(null);
            binding.layoutNamefile.setVisibility(View.GONE);
        });


        return binding.getRoot();
    }


    public RequestBody File_form(File req) {
        return RequestBody.create(req, MediaType.parse("multipart/form-data"));
    }

    public void OpenManager(){
        openFileManager = new Intent(Intent.ACTION_GET_CONTENT);
        openFileManager.setType("application/pdf");
        startActivityForResult(openFileManager, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PRIVATE_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                MDToast.makeText(requireContext(), "Permission Granted", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
            }
            else {
                Objects.requireNonNull(getDialog()).cancel();
            }
        }
    }

    public void uploadCameras(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 2);
    }

    public void openSetting(){
        Intent setting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+ requireActivity().getPackageName()));
        setting.addCategory(Intent.CATEGORY_DEFAULT);
        setting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(setting,REQUEST_SETTING);
    }

    public void Permission(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Log.i("home", "Permission Storage: " + true);
            OpenManager();
        } else if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
            MDToast.makeText(requireContext(), "You Must Have Enable Permission", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
            openSetting();
        }
        else {
            IjinStorage();
        }
    }

    public void IjinStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(requireContext()).setTitle("Permission Required")
                    .setMessage("Anda harus memberikan ijin untuk akses File Manager")
                    .setPositiveButton("Yes", (dialog, which) -> ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PRIVATE_CODE))
                    .setNegativeButton("No", (dialog, which) ->{
                        dialog.dismiss();
                        Objects.requireNonNull(getDialog()).cancel();
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PRIVATE_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            try {
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    Log.i(TAG, "ini files getting nya : " + data.getData());
                    Uri path = data.getData();

                    try {
                        files = ConvertFiles.from(Objects.requireNonNull(getContext()),path);
                        String name = files.getName();
                        int size = (int) files.length() / 1024;
                        if (files != null) {
                            binding.nameFile.setText(name);
                            binding.layoutNamefile.setVisibility(View.VISIBLE);
                            binding.uploadFile.setVisibility(View.GONE);
                            binding.iconCameraUpload.setVisibility(View.GONE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {}
        } else {
            try {
                if (resultCode == RESULT_OK){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(getContext(), photo);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    files = new File(getRealPathFromURI(tempUri));
                    if (files != null) {
                        binding.nameFile.setText("Images.jpg");
                        binding.layoutNamefile.setVisibility(View.VISIBLE);
                        binding.uploadFile.setVisibility(View.GONE);
                        binding.iconCameraUpload.setVisibility(View.GONE);
                    }
                    Log.i(TAG, "check dari kamera: " + files);
                }
            } catch (Exception err){

            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void UploadSuratPanmud(){
        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("surat", files.getPath(), File_form(files));
        }

        Call<SuratModel.Alone> call = BaseModel.i.getService().AddPanmudSurat(BaseModel.i.token,
                Objects.requireNonNull(binding.title.getText()).toString(),perkara.id, fileToUpload);
        call.enqueue(new Callback<SuratModel.Alone>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<SuratModel.Alone> call, @NotNull Response<SuratModel.Alone> response) {
                SuratModel.Alone data = response.body();
                if (Calling.TreatResponse(requireContext(), "Upload Surat", data)){
                    assert data != null;
//                    PerkaraModel.perkaradiproses.removeIf(ise -> ise.perkara_id == data.surat.perkara_id);
                    binding.progress.setVisibility(View.VISIBLE);
                    MDToast.makeText(requireContext(), "Berhasil menambahkan Surat!", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Document document = (Layer_Document) getFragmentManager().findFragmentByTag("document");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert document != null;
                    transaction.detach(document);
                    transaction.attach(document);
                    transaction.commit();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<SuratModel.Alone> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void UploadSuratJurusita(){
        MultipartBody.Part fileToUpload = null;
        if (files != null){
//            verifyStoragePermissions(getActivity());
            fileToUpload = MultipartBody.Part.createFormData("surat", files.getPath(), File_form(files));
        }

        Call<SuratModel.Alone> call = BaseModel.i.getService().AddJurusitaSurat(BaseModel.i.token,surat.id, fileToUpload);
        call.enqueue(new Callback<SuratModel.Alone>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<SuratModel.Alone> call, @NotNull Response<SuratModel.Alone> response) {
                SuratModel.Alone data = response.body();
                if (Calling.TreatResponse(requireContext(), "Upload Surat", data)){
                    assert data != null;
                    binding.progress.setVisibility(View.VISIBLE);
                    MDToast.makeText(requireContext(), "Berhasil menambahkan Surat!", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Document document = (Layer_Document) getFragmentManager().findFragmentByTag("document");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert document != null;
                    surat.daftar_pengantar = files.getName();
                    transaction.detach(document);
                    transaction.attach(document);
                    transaction.commit();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<SuratModel.Alone> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void VerifyPPK(){
        Call<MessageModel> call = BaseModel.i.getService().VerifyPPK(BaseModel.i.token,surat.id);
        call.enqueue(new Callback<MessageModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Upload Surat", data)){
                    assert data != null;
                    binding.progress.setVisibility(View.VISIBLE);
                    MDToast.makeText(requireContext(), data.data, Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Document document = (Layer_Document) getFragmentManager().findFragmentByTag("document");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert document != null;
                    surat.verifier_id = UserModel.i.id;
                    surat.fullname_ppk = UserModel.i.fullname;
                    transaction.detach(document);
                    transaction.attach(document);
                    transaction.commit();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void MintaBayar(){
        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("bayar", files.getPath(), File_form(files));
        }
        Call<MessageModel> call = BaseModel.i.getService().BayarCreate(BaseModel.i.token,surat.id,fileToUpload);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel data = response.body();
                if (Calling.TreatResponse(requireContext(),"Bayar Create", data)){
                    assert data != null;
                    MDToast.makeText(requireContext(),data.data, Toast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Document document = (Layer_Document) getFragmentManager().findFragmentByTag("document");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert document != null;
//                    anggaran.model.add()
                    transaction.detach(document);
                    transaction.attach(document);
                    transaction.commit();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void VerifikasiBendahara(){
        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("bukti", files.getPath(), File_form(files));
        }
        Call<PembayaranModel.Alone> call = BaseModel.i.getService().BendaharaVerif(BaseModel.i.token,bayar.id,fileToUpload);
        call.enqueue(new Callback<PembayaranModel.Alone>() {
            @Override
            public void onResponse(@NotNull Call<PembayaranModel.Alone> call, @NotNull Response<PembayaranModel.Alone> response) {
                PembayaranModel.Alone model = response.body();
                if (Calling.TreatResponse(requireContext(),"Bayar Create", model)){
                    assert model != null;
                    MDToast.makeText(requireContext(),"Successfuly Verifikasi Pembayaran", Toast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Anggaran anggaran = (Layer_Anggaran) getFragmentManager().findFragmentByTag("anggaran");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert anggaran != null;
//                    anggaran.model.get(position).kuitansi = bayar.kuitansi;
                    transaction.detach(anggaran);
                    transaction.attach(anggaran);
                    transaction.commit();
                    dismiss();
                }
                binding.progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<PembayaranModel.Alone> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                binding.progress.setVisibility(View.GONE);
            }
        });
    }
}
