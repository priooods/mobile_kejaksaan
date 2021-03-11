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
import com.prio.kejaksaan.model.UsersModel;
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
    UsersModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);
        binding.btnSignIn.setOnClickListener(v -> {
            model = new UsersModel(Objects.requireNonNull(binding.name.getText()).toString(),
                    Objects.requireNonNull(binding.password.getText()).toString());
            services = model.getService();
            LoginAccount();
        });
    }

    public void LoginAccount(){
        Call<UsersModel> call = services.Login(model.name, model.password);
        call.enqueue(new Callback<UsersModel>() {
            @Override
            public void onResponse(@NotNull Call<UsersModel> call, @NotNull Response<UsersModel> response) {
                UsersModel usersModel = (UsersModel)response.body();
                if (UsersModel.TreatResponse(Login.this, "login", usersModel)){
                    assert usersModel != null;
                    model.setToken(usersModel.token);
                    UsersModel.i = model;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", usersModel.token);
                    editor.putString("name", usersModel.name);
                    editor.apply();
                    goHome();
                }
            }

            @Override
            public void onFailure(@NotNull Call<UsersModel> call, @NotNull Throwable t) {
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
        if (sharedPreferences.getString("token", null) != null){
            goHome();
        }
    }
}