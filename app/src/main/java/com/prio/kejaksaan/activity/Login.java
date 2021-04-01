package com.prio.kejaksaan.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ActivityMainBinding;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.service.UserService;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    ActivityMainBinding binding;
    UserService services;
    SharedPreferences sharedPreferences;
    BaseModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnSignIn.setOnClickListener(v -> {
            BaseModel.i = model =  new BaseModel();
            services = model.getService();
            LoginAccount(Objects.requireNonNull(binding.name.getText()).toString(), Objects.requireNonNull(binding.password.getText()).toString());
        });
    }

    public void LoginAccount(String username, String password){
        Call<UserModel> call;
        if (password.equals("")) {
            call = services.Login("prio" + username, "prio123");//model.password);
        }else{
            call = services.Login(username, password);
        }
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NotNull Call<UserModel> call, @NotNull Response<UserModel> response) {
                UserModel usersModel = response.body();
                if (Calling.TreatResponse(Login.this, "login", usersModel)){
                    assert usersModel != null;
                    UserModel.i = usersModel;
                    model.token = usersModel.token;
                    SharedPreferences.Editor editor =  sharedPreferences.edit();
                    editor.putString("token", usersModel.token);
                    editor.putString("name", usersModel.fullname);
                    editor.putString("type", usersModel.type);
                    editor.apply();
                    goHome();
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserModel> call, @NotNull Throwable t) {
                Log.i("Error", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void goHome(){
        startActivity(new Intent(Login.this, BaseActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.getString("token", null) != null)
            goHome();
    }
}