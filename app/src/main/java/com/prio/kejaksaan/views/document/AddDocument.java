package com.prio.kejaksaan.views.document;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.prio.kejaksaan.layer.Layer_Document;
import com.prio.kejaksaan.layer.Layer_Perkara;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.PerkaraModel;
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
    Intent openFileManager;
    File files;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddSuratBinding.inflate(inflater, container, false);

        switch (DocumentModel.ShowDetailDocument){
            case 1: //ini untuk panmud upload files
                binding.nama.setText(PerkaraModel.i.identitas);
                binding.dakwaan.setText(PerkaraModel.i.dakwaan);
                binding.nomor.setText(PerkaraModel.i.nomor);
                binding.jenisPerkara.setText(PerkaraModel.i.jenis);
                binding.tanggal.setText(PerkaraModel.i.tanggal);
                binding.penahanan.setText(PerkaraModel.i.penahanan);
                binding.ppName.setText(PerkaraModel.i.fullname_pp);
                binding.jurusitaName.setText(PerkaraModel.i.fullname_jurusita);
                binding.l9.setVisibility(View.GONE);
                binding.l10.setVisibility(View.GONE);
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
                binding.nama.setText(DocumentModel.i.identitas);
                binding.dakwaan.setText(DocumentModel.i.dakwaan);
                binding.nomor.setText(DocumentModel.i.nomor);
                binding.jenisPerkara.setText(DocumentModel.i.jenis);
                binding.tanggal.setText(DocumentModel.i.tanggal);
                binding.penahanan.setText(DocumentModel.i.penahanan);
                binding.ppName.setText(DocumentModel.i.fullname_pp);
                binding.jurusitaName.setText(DocumentModel.i.fullname_jurusita);
                binding.l9.setVisibility(View.VISIBLE);
                if (DocumentModel.i.daftar_pengantar != null){
                    binding.l10.setVisibility(View.VISIBLE);
                }
                binding.letter.setText(DocumentModel.i.surat_tugas);
                binding.pengantarFiles.setText(DocumentModel.i.daftar_pengantar);

                binding.letter.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+DocumentModel.i.surat_tugas));
                    startActivity(web);
                });
                binding.pengantarFiles.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+DocumentModel.i.daftar_pengantar));
                    startActivity(web);
                });
                break;
            case 3: // ini untuk jurusita upload files
                binding.top.setText("Upload Letter");
                binding.top2.setText("Click open files to selected files on your device");
                binding.titleLayout.setVisibility(View.GONE);
                binding.a.setVisibility(View.GONE);
                binding.l0.setVisibility(View.GONE);
                binding.l2.setVisibility(View.GONE);
                binding.l3.setVisibility(View.GONE);
                binding.l4.setVisibility(View.GONE);
                binding.l5.setVisibility(View.GONE);
                binding.l6.setVisibility(View.GONE);
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
                break;
            case 4: // Ini untuk add verify PPK
                binding.top.setText("Verify Perkara");
                binding.top2.setText("Click on button if you want verify perkara");
                binding.l1.setVisibility(View.GONE);
                binding.nama.setText(PerkaraModel.i.identitas);
                binding.dakwaan.setText(PerkaraModel.i.dakwaan);
                binding.nomor.setText(PerkaraModel.i.nomor);
                binding.jenisPerkara.setText(PerkaraModel.i.jenis);
                binding.tanggal.setText(PerkaraModel.i.tanggal);
                binding.penahanan.setText(PerkaraModel.i.penahanan);
                binding.l7.setVisibility(View.GONE);
                binding.l8.setVisibility(View.GONE);
                binding.l9.setVisibility(View.VISIBLE);
                binding.l10.setVisibility(View.VISIBLE);
                binding.letter.setText(PerkaraModel.i.surat_tugas);
                binding.pengantarFiles.setText(PerkaraModel.i.daftar_pengantar);
                binding.letter.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+PerkaraModel.i.surat_tugas));
                    startActivity(web);
                });
                binding.pengantarFiles.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+PerkaraModel.i.daftar_pengantar));
                    startActivity(web);
                });
                binding.btnCreateletter.setText("Verify Perkara");
                binding.btnCreateletter.setOnClickListener(v -> VerifyPPK());
                break;
            case 5:
                binding.top.setText("Detail Verify");
                binding.top2.setText("Click on document name if you want getting files");
                binding.l1.setVisibility(View.GONE);
                binding.btnCreateletter.setVisibility(View.GONE);
                binding.nama.setText(DocumentModel.i.identitas);
                binding.dakwaan.setText(DocumentModel.i.dakwaan);
                binding.nomor.setText(DocumentModel.i.nomor);
                binding.jenisPerkara.setText(DocumentModel.i.jenis);
                binding.tanggal.setText(DocumentModel.i.tanggal);
                binding.penahanan.setText(DocumentModel.i.penahanan);
                binding.identitas1.setText("Indentias PPK");
                binding.ppName.setText(DocumentModel.i.fullname);
                binding.l8.setVisibility(View.GONE);
                binding.l9.setVisibility(View.VISIBLE);
                binding.l10.setVisibility(View.VISIBLE);
                binding.letter.setText(DocumentModel.i.surat_tugas);
                binding.pengantarFiles.setText(DocumentModel.i.daftar_pengantar);
                binding.letter.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+DocumentModel.i.surat_tugas));
                    startActivity(web);
                });
                binding.pengantarFiles.setOnClickListener(v -> {
                    Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+DocumentModel.i.daftar_pengantar));
                    startActivity(web);
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
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri path = data.getData();

                String filePath = RealPathUtil.getRealPathFromURI_API19(requireContext(), path);
                assert filePath != null;
                files = new File(filePath);

                String name = files.getName();
                int size = (int) files.length() / 1024;
                Log.i(TAG, "file: " + "name = " + name + " size = " + size);
                if (files != null){
                    binding.nameFile.setText(name);
                    binding.layoutNamefile.setVisibility(View.VISIBLE);
                    binding.uploadFile.setVisibility(View.GONE);
                }

            }
        }
    }

    public void UploadSuratPanmud(){
        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("surat", files.getPath(), File_form(files));
        }

        Call<DocumentModel> call = BaseModel.i.getService().AddPanmudSurat(BaseModel.i.token,
                Objects.requireNonNull(binding.title.getText()).toString(),PerkaraModel.i.perkara_id, fileToUpload);
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
            fileToUpload = MultipartBody.Part.createFormData("surat", files.getPath(), File_form(files));
        }

        Call<DocumentModel> call = BaseModel.i.getService().AddJurusitaSurat(BaseModel.i.token,PerkaraModel.i.id, fileToUpload);
        call.enqueue(new Callback<DocumentModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<DocumentModel> call, @NotNull Response<DocumentModel> response) {
                DocumentModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Upload Surat", data)){
                    assert data != null;
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

    public void VerifyPPK(){
        Call<PerkaraModel> call = BaseModel.i.getService().VerifyPPK(BaseModel.i.token,PerkaraModel.i.id);
        call.enqueue(new Callback<PerkaraModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Upload Surat", data)){
                    assert data != null;
                    binding.progress.setVisibility(View.VISIBLE);
                    MDToast.makeText(requireContext(), "Successfully Veriry Perkara ! Please Refresh Pages", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
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
