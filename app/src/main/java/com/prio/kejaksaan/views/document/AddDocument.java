package com.prio.kejaksaan.views.document;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.DialogAddSuratBinding;
import com.prio.kejaksaan.layer.Layer_Anggaran;
import com.prio.kejaksaan.layer.Layer_Document;
import com.prio.kejaksaan.layer.Layer_Perkara;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.SuratModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.RealPathUtil;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
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

    String[] listType = {"Pengiriman penetapan hari sidang","Pengiriman petikan / salinan putusan",
            "Pengiriman surat penahanan & perpanjangan penahanan","Pengiriman salinan putusan",
            "Pemberitahuan proses banding","Pemberitahuan putusan banding","Pemberitahuan proses kasasi","Pemberitahuan putusan kasasi"};
    DialogAddSuratBinding binding;
    public static int REQUEST_SETTING = 168;
    public static int PRIVATE_CODE = 1;
    public int mode;
    SuratModel.Item surat;
    PerkaraListModel.Item perkara;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddSuratBinding.inflate(inflater, container, false);
//        PerkaraListModel.Item perkara;
        switch (mode){
            case 1: //ini untuk panmud upload files
                binding.nama.setText(perkara.identitas);
                binding.dakwaan.setText(perkara.dakwaan);
                binding.nomor.setText(perkara.nomor);
                binding.jenisPerkara.setText(perkara.jenis);
                binding.tanggal.setText(perkara.tanggal);
                binding.penahanan.setText(perkara.penahanan);
                binding.ppName.setText(perkara.fullname_pp);
                binding.jurusitaName.setText(perkara.fullname_jurusita);
                binding.surat.setVisibility(View.GONE);
                binding.l10.setVisibility(View.GONE);
                binding.titleLayout.setVisibility(View.VISIBLE);
                binding.btnCreateletter.setOnClickListener(v -> {
                    if (Objects.requireNonNull(binding.title.getText()).toString().isEmpty() || files == null){
                        MDToast.makeText(requireContext(), "Please Completely all forms", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    } else {
                        binding.progress.setVisibility(View.VISIBLE);
                        UploadSuratPanmud();
                    }
                });
                break;
            case 2: //ini untuk panmud detail sudah ada files di upload
                binding.top.setText("Detail Document");
                binding.top2.setText("Click on document name if you want getting files");
                binding.l1.setVisibility(View.GONE);
                binding.btnCreateletter.setVisibility(View.GONE);
                perkara = surat.perkara;
                binding.nama.setText(perkara.identitas);
                binding.dakwaan.setText(perkara.dakwaan);
                binding.nomor.setText(perkara.nomor);
                binding.jenisPerkara.setText(perkara.jenis);
                binding.tanggal.setText(perkara.tanggal);
                binding.penahanan.setText(perkara.penahanan);
                binding.ppName.setText(perkara.fullname_pp);
                binding.jurusitaName.setText(perkara.fullname_jurusita);
                binding.surat.setVisibility(View.VISIBLE);
                if (surat.daftar_pengantar == null){
                    binding.btnShowb.setVisibility(View.GONE);
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
                binding.top.setText("Detail Surat");
                binding.top2.setText("Pilih file untuk mengirimkan bukti pengantar (bukti perjalanan dinas)");
                perkara = surat.perkara;
                binding.nama.setText(perkara.identitas);
                binding.dakwaan.setText(perkara.dakwaan);
                binding.nomor.setText(perkara.nomor);
                binding.jenisPerkara.setText(perkara.jenis);
                binding.tanggal.setText(perkara.tanggal);
                binding.penahanan.setText(perkara.penahanan);
//                binding.l1.setVisibility(View.VISIBLE);
//                binding.
                binding.l7.setVisibility(View.GONE);
                binding.l8.setVisibility(View.GONE);
                binding.btnCreateletter.setOnClickListener(v -> {
                    if (files == null){
                        MDToast.makeText(requireContext(), "Please Selected files", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    } else {
                        binding.progress.setVisibility(View.VISIBLE);
                        UploadSuratJurusita();
                    }
                });
//                binding.btnCreateletter.setOnClickListener(v -> UploadSuratJurusita());
                binding.surat.setVisibility(View.VISIBLE);
                binding.btnShowa.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+surat.surat_tugas));
                    startActivity(web);
                });
                binding.btnShowb.setVisibility(View.GONE);
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
                binding.top.setText("Detail Surat");
                binding.top2.setText("Pilih nama dokumen untuk mendapatkan file surat");
                perkara = surat.perkara;
                binding.nama.setText(perkara.identitas);
                binding.dakwaan.setText(perkara.dakwaan);
                binding.nomor.setText(perkara.nomor);
                binding.jenisPerkara.setText(perkara.jenis);
                binding.tanggal.setText(perkara.tanggal);
                binding.penahanan.setText(perkara.penahanan);
                binding.l10.setVisibility(View.VISIBLE);

                binding.titleLayout.setVisibility(View.GONE);
                binding.ppName.setText(perkara.fullname_pp);
                binding.jurusitaName.setText(perkara.fullname_jurusita);
                binding.btnCreateletter.setOnClickListener(v -> VerifyPPK());
                binding.btnCreateletter.setText("Verifikasi!");
                binding.surat.setVisibility(View.VISIBLE);
                binding.btnShowa.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+surat.surat_tugas));
                    startActivity(web);
                });
                binding.btnShowb.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+surat.daftar_pengantar));
                    startActivity(web);
                });
                break;
            case 6: //ini untuk PPK upload pembayaran
                binding.a.setVisibility(View.GONE);
                binding.l0.setVisibility(View.GONE);
                binding.l2.setVisibility(View.GONE);
                binding.l3.setVisibility(View.GONE);
                binding.l4.setVisibility(View.GONE);
                binding.l5.setVisibility(View.GONE);
                binding.l6.setVisibility(View.GONE);
                binding.l7.setVisibility(View.GONE);
                binding.l8.setVisibility(View.GONE);
                binding.l10.setVisibility(View.GONE);
                binding.surat.setVisibility(View.GONE);
                binding.top2.setText("Upload files PDF untuk pemintaan pembiayaan kepada bendahara");
                binding.titleLayout.setVisibility(View.GONE);
                binding.btnCreateletter.setOnClickListener(v -> {
                    if (files == null){
                        MDToast.makeText(requireContext(), "Please Completely all forms", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    } else {
                        binding.progress.setVisibility(View.VISIBLE);
                        MintaBayar();
                    }
                });
                break;
            case 7: // ini untuk bendahara verified permintaan
                binding.a.setVisibility(View.GONE);
                binding.l0.setVisibility(View.GONE);
                binding.l2.setVisibility(View.GONE);
                binding.l3.setVisibility(View.GONE);
                binding.l4.setVisibility(View.GONE);
                binding.l5.setVisibility(View.GONE);
                binding.l6.setVisibility(View.GONE);
                binding.l7.setVisibility(View.GONE);
                binding.l8.setVisibility(View.GONE);
                binding.l10.setVisibility(View.GONE);
                binding.surat.setVisibility(View.GONE);
                binding.top2.setText("Upload files PDF untuk verifikasi permintaan pembiayaan !");
                binding.titleLayout.setVisibility(View.GONE);
                binding.btnCreateletter.setOnClickListener(v -> {
                    if (files == null){
                        MDToast.makeText(requireContext(), "Please Completely all forms", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                    } else {
                        binding.progress.setVisibility(View.VISIBLE);
                        VerifikasiBendahara();
                    }
                });
                break;
        }

        binding.title.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, listType);
        binding.title.setAdapter(adapter);
        binding.uploadFile.setOnClickListener(v -> Permission());
        binding.deleteFile.setOnClickListener(v -> {
            binding.uploadFile.setVisibility(View.VISIBLE);
            files = null;
            binding.nameFile.setText(null);
            binding.layoutNamefile.setVisibility(View.GONE);
        });

        binding.deleteFile.setOnClickListener(v -> {
            binding.uploadFile.setVisibility(View.VISIBLE);
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
                    .setMessage("You must give document opening permission to upload files")
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
//                verifyStoragePermissions(getActivity());
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    Uri path = data.getData();

                    String filePath = RealPathUtil.getRealPathFromURI_API19(requireContext(), path);
                    assert filePath != null;
                    files = new File(filePath);

                    String name = files.getName();
                    int size = (int) files.length() / 1024;
                    Log.i(TAG, "file: " + "name = " + name + " size = " + size);
                    if (files != null) {
                        binding.nameFile.setText(name);
                        binding.layoutNamefile.setVisibility(View.VISIBLE);
                        binding.uploadFile.setVisibility(View.GONE);
                    }

                }
            } catch (Exception e) {}
        }
    }

    public void UploadSuratPanmud(){
        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("surat", files.getPath(), File_form(files));
        }

        Call<DocumentModel> call = BaseModel.i.getService().AddPanmudSurat(BaseModel.i.token,
                Objects.requireNonNull(binding.title.getText()).toString(),perkara.id, fileToUpload);
        call.enqueue(new Callback<DocumentModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<DocumentModel> call, @NotNull Response<DocumentModel> response) {
                DocumentModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Upload Surat", data)){
                    assert data != null;
                    PerkaraModel.perkaradiproses.removeIf(ise -> ise.perkara_id == data.surat.perkara_id);
                    binding.progress.setVisibility(View.VISIBLE);
                    MDToast.makeText(requireContext(), "Successfully add letter !", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
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
            public void onFailure(@NotNull Call<DocumentModel> call, @NotNull Throwable t) {
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
                    MDToast.makeText(requireContext(), "Successfully add letter !", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
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
        Call<PerkaraModel> call = BaseModel.i.getService().VerifyPPK(BaseModel.i.token,surat.id);
        call.enqueue(new Callback<PerkaraModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Upload Surat", data)){
                    assert data != null;
                    binding.progress.setVisibility(View.VISIBLE);
                    MDToast.makeText(requireContext(), "Successfully Veriry Perkara ! Please Refresh Pages", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
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
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void MintaBayar(){
        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("bayar", files.getPath(), File_form(files));
        }
        Call<PerkaraModel> call = BaseModel.i.getService().BayarCreate(BaseModel.i.token,surat.id,fileToUpload);
        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel data = response.body();
                if (Calling.TreatResponse(requireContext(),"Bayar Create", data)){
                    assert data != null;
                    MDToast.makeText(requireContext(),"\"Successfuly Verifikasi Pembayaran\"", Toast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
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
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void VerifikasiBendahara(){
        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("bukti", files.getPath(), File_form(files));
        }
        Call<PerkaraModel> call = BaseModel.i.getService().BendaharaVerif(BaseModel.i.token,AtkModel.i.id,fileToUpload);
        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel perkaraModel = response.body();
                if (Calling.TreatResponse(requireContext(),"Bayar Create", perkaraModel)){
                    assert perkaraModel != null;
                    MDToast.makeText(requireContext(),"Successfuly Verifikasi Pembayaran", Toast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Anggaran anggaran = (Layer_Anggaran) getFragmentManager().findFragmentByTag("anggaran");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert anggaran != null;
                    transaction.detach(anggaran);
                    transaction.attach(anggaran);
                    transaction.commit();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}
