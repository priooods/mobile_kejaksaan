package com.prio.kejaksaan.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ActivityBaseBinding;
import com.prio.kejaksaan.layer.Layer_Anggaran;
import com.prio.kejaksaan.layer.Layer_Document;
import com.prio.kejaksaan.layer.Layer_Home;
import com.prio.kejaksaan.layer.Layer_Perkara;
import com.prio.kejaksaan.layer.Layer_Persediaan;
import com.prio.kejaksaan.layer.Layer_Profile;
import com.prio.kejaksaan.layer.Layer_Report;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

public class BaseActivity extends AppCompatActivity {

    ActivityBaseBinding binding;
    SharedPreferences sharedPreferences;
    public static int PRIVATE_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Permission();

        binding.progress.setVisibility(View.VISIBLE);
        binding.bottomNavigation.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);

        if (!BaseModel.isExist() || UserModel.i == null) {
            BaseModel.i = new BaseModel(sharedPreferences.getString("token", null));
            GettingDetail();
        }else{
            Dashboard();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new Layer_Home(),"home").commit();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment select;
            switch (item.getItemId()){
                case R.id.home:
                    select =  new Layer_Home();
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, select, "home").commit();
                    break;
                case R.id.persediaan:
                    select = new Layer_Persediaan();
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, select, "persediaan").commit();
                    break;
                case R.id.perkara:
                    select = new Layer_Perkara();
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, select, "perkara").commit();
                    break;
                case R.id.document:
                    select = new Layer_Document();
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, select, "document").commit();
                    break;
                case R.id.anggaran:
                    select = new Layer_Anggaran();
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, select, "anggaran").commit();
                    break;
            }
            return true;
        });
    }

    public void GettingDetail(){
        Call<UserModel> call = BaseModel.i.getService().UserDetail(BaseModel.i.token);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                UserModel data = response.body();
                if (Calling.TreatResponse(BaseActivity.this, "profile", data)) {
                    assert data != null;
                    UserModel.i = data.data;
                    Dashboard();
                }else{
                    binding.progress.setVisibility(View.GONE);
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                    goLogin();
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
    private void goLogin(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(BaseActivity.this,Login.class));
        finish();
    }
    public void Dashboard(){
        binding.progress.setVisibility(View.GONE);
        binding.bottomNavigation.setVisibility(View.VISIBLE);
        switch (sharedPreferences.getString("type", null)){
            case "PP":
                binding.bottomNavigation.getMenu().findItem(R.id.document).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.anggaran).setVisible(false);
                break;
            case "Jurusita":
                binding.bottomNavigation.getMenu().findItem(R.id.persediaan).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.anggaran).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.perkara).setVisible(false);
                break;
            case "Panitera":
                binding.bottomNavigation.getMenu().findItem(R.id.persediaan).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.document).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.anggaran).setVisible(false);
                break;
            case "Panmud":
                binding.bottomNavigation.getMenu().findItem(R.id.perkara).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.persediaan).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.anggaran).setVisible(false);
                break;
            case "PPK":
                binding.bottomNavigation.getMenu().findItem(R.id.perkara).setVisible(false);
                break;
            case "Pengelola Persediaan":
                binding.bottomNavigation.getMenu().findItem(R.id.perkara).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.document).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.anggaran).setVisible(false);
                break;
            case "Bendahara":
                binding.bottomNavigation.getMenu().findItem(R.id.perkara).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.document).setVisible(false);
                binding.bottomNavigation.getMenu().findItem(R.id.persediaan).setVisible(false);
                break;
            case "KPA":
            case "SuperUser":
                binding.bottomNavigation.getMenu().findItem(R.id.document).setVisible(false);
                break;
        }
    }


    private  boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.CAMERA);
        int locationStorage = ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(BaseActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),PRIVATE_CODE);
            return false;
        }
        return true;
    }

    public void Permission(){
        checkAndRequestPermissions();
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//            Log.i("home", "Permission Storage: " + true);
//        } else {
//            IjinStorage();
//        }
    }

    public void IjinStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permission Required")
                    .setMessage("You must give document opening permission to upload files")
                    .setPositiveButton("Yes", (dialog, which) -> ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PRIVATE_CODE))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PRIVATE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PRIVATE_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                MDToast.makeText(this, "Permission Success", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
            }
            else {
                MDToast.makeText(this, "Permission Not Given", MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
            }
        }
    }
}
