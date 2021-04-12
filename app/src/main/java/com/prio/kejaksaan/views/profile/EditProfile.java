package com.prio.kejaksaan.views.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.DialogEditProfileBinding;
import com.prio.kejaksaan.layer.Layer_Home;
import com.prio.kejaksaan.layer.Layer_Profile;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.MessageModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.RealPathUtil;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class EditProfile extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullscreen);
    }

    DialogEditProfileBinding binding;
    SharedPreferences sharedPreferences;
    String[] listType = {"Ketua","Panitera","Kuasa Pengguna Anggaran","Panitera Muda","Panitera Pengganti","Jurusita","Pejabat Pembuat Komitmen","Bendahara","Pengelola Persediaan"};

//    String[] listType = {"Ketua","Panitera","KPA","Panmud","PP","Jurusita","PPK","Bendahara","Pengelola Persediaan"};
    public static int REQUEST_SETTING = 168;
    public static int PRIVATE_CODE = 1;
    Intent openFileManager;
    File files;
    String types;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEditProfileBinding.inflate(inflater,container,false);
        sharedPreferences = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        binding.btnShowEditing.setOnClickListener(v->{
            binding.showEditingLayout.setVisibility(View.VISIBLE);
            binding.layoutDetailProfile.setVisibility(View.GONE);
        });

        binding.userFullname.setText(UserModel.i.fullname);
        binding.userUsername.setText(UserModel.i.name);
        binding.userType.setText(UserModel.i.type);


        if (UserModel.i.avatar != null) {
            Glide.with(this).load("https://digitalsystemindo.com/jaksa/public/images/" + UserModel.i.avatar)
                    .circleCrop().into(binding.avatar);
        }
        binding.name.setText(UserModel.i.name);
        binding.fullname.setText(UserModel.i.fullname);
        binding.password.setText(UserModel.i.password_verified);
        binding.backpress.setOnClickListener(v->dismiss());
//        if (UserModel.i.type.equals("SuperUser") || UserModel.i.type.equals("KPA") ){
//            binding.typeLayout.setVisibility(View.VISIBLE);
//        }
        binding.type.setText(UserModel.i.type);

        binding.type.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, listType);
        binding.type.setAdapter(adapter);
        binding.avatar.setOnClickListener(v ->
            Permission()
        );

        binding.btnCancelEdit.setOnClickListener(v->{
            binding.showEditingLayout.setVisibility(View.GONE);
            binding.layoutDetailProfile.setVisibility(View.VISIBLE);
        });
        if (UserModel.TypeCreateUser == 90){
            binding.typeLayout.setVisibility(View.VISIBLE);
            binding.btnCreateUsers.setOnClickListener(v -> {
                checckusertype();
                EditUserforKPA();
            });
            binding.btnDeleted.setVisibility(View.VISIBLE);
            binding.btnDeleted.setOnClickListener(v-> DeleteUsers());
        } else{
            binding.btnCreateUsers.setOnClickListener(v -> {
                checckusertype();
                updateDataUser();
            });
        }

        return binding.getRoot();
    }

    public RequestBody File_form(File req) {
        return RequestBody.create(req, MediaType.parse("multipart/form-data"));
    }

    public RequestBody Input_form(String req) {
        return RequestBody.create(req, MediaType.parse("multipart/form-data"));
    }

    public void OpenManager(){
        openFileManager = new Intent(Intent.ACTION_GET_CONTENT);
        openFileManager.setType("image/*");
        startActivityForResult(openFileManager, 1);
    }

    public void checckusertype (){
        assert binding != null;
        switch (binding.type.getText().toString()){
            case "Kuasa Pengguna Anggaran":
                types = "KPA";
                break;
            case "Panitera Muda":
                types = "Panmud";
                break;
            case "Pejabat Pembuat Komitmen":
                types = "PPK";
                break;
            case "Panitera Pengganti":
                types = "PP";
                break;
            default:
                types = binding.type.getText().toString();
                break;
        }
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
                Glide.with(requireContext()).load(files).circleCrop().into(binding.avatar);
            }
        }
    }

    public void updateDataUser(){
        HashMap<String, RequestBody> Profile = new HashMap<>();
        Profile.put("name", Input_form(Objects.requireNonNull(binding.name.getText()).toString()));
        if (sharedPreferences.getString("type",null).equals("SuperUser")
                || sharedPreferences.getString("type",null).equals("KPA")){
            Profile.put("type", Input_form(types));
        }
        Profile.put("fullname", Input_form(Objects.requireNonNull(binding.fullname.getText()).toString()));
        Profile.put("password", Input_form(Objects.requireNonNull(binding.password.getText()).toString()));


        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("avatar", files.getPath(), File_form(files));
        }

        Call<BaseModel> call = BaseModel.i.getService().updateUser(BaseModel.i.token, Profile, fileToUpload);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                BaseModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Update Profile", data)){
                    MDToast.makeText(requireContext(),"Successfully Update User Profile", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Home profile = (Layer_Home) getFragmentManager().findFragmentByTag("home");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert profile != null;
                    transaction.detach(profile);
                    transaction.attach(profile);
                    transaction.commit();
                    Objects.requireNonNull(getDialog()).dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {

            }
        });

    }

    public void EditUserforKPA(){
        HashMap<String, RequestBody> Profile = new HashMap<>();
        Profile.put("name", Input_form(Objects.requireNonNull(binding.name.getText()).toString()));
        if (sharedPreferences.getString("type",null).equals("SuperUser")
                || sharedPreferences.getString("type",null).equals("KPA")){
            Profile.put("type", Input_form(types));
        }
        Profile.put("fullname", Input_form(Objects.requireNonNull(binding.fullname.getText()).toString()));
        Profile.put("password", Input_form(Objects.requireNonNull(binding.password.getText()).toString()));


        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("avatar", files.getPath(), File_form(files));
        }

        Call<BaseModel> call = BaseModel.i.getService().EditUser(BaseModel.i.token, UserModel.i.id, Profile, fileToUpload);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                BaseModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Update Profile", data)){
                    Log.i(TAG, "onResponse: " + "Updated Userss Succeess gaes");
                    MDToast.makeText(requireContext(),"Successfully Update User Profile", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    binding.layoutDetailProfile.setVisibility(View.VISIBLE);
                    binding.showEditingLayout.setVisibility(View.GONE);
                    //                    assert getFragmentManager() != null;
//                    Layer_Home profile = (Layer_Home) getFragmentManager().findFragmentByTag("home");
//                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
//                    assert profile != null;
//                    transaction.detach(profile);
//                    transaction.attach(profile);
//                    transaction.commit();
//                    Objects.requireNonNull(getDialog()).dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {

            }
        });

    }


    public void DeleteUsers(){
        Call<MessageModel> call = BaseModel.i.getService().DeleteUser(UserModel.i.id,BaseModel.i.token);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Delete Profile", data)){
                    Log.i(TAG, "onResponse: " + "Delete Users Succeess gaes");
                    MDToast.makeText(requireContext(),"Successfully Delete User", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Home profile = (Layer_Home) getFragmentManager().findFragmentByTag("home");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert profile != null;
                    transaction.detach(profile);
                    transaction.attach(profile);
                    transaction.commit();
                    Objects.requireNonNull(getDialog()).dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {

            }
        });

    }
}
