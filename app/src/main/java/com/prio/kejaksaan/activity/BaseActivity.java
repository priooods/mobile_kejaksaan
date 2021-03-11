package com.prio.kejaksaan.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ActivityBaseBinding;
import com.prio.kejaksaan.layer.Layer_Document;
import com.prio.kejaksaan.layer.Layer_Home;
import com.prio.kejaksaan.layer.Layer_Profile;
import com.prio.kejaksaan.layer.Layer_Report;
import com.prio.kejaksaan.model.UsersModel;

public class BaseActivity extends AppCompatActivity {

    ActivityBaseBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        sharedPreferences = getSharedPreferences("session", Context.MODE_PRIVATE);

        if (UsersModel.isExist()) {
            Log.i("home", "onCreate: " + "Ada");
            UsersModel.i.setToken(sharedPreferences.getString("token", null));
        } else {
            UsersModel data = new UsersModel(sharedPreferences.getString("token",null));
            UsersModel.i = data;
            Log.i("home", "token: " + data.token);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new Layer_Home()).commit();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment select = null;
            switch (item.getItemId()){
                case R.id.home:
                    select = new Layer_Home();
                    break;
                case R.id.document:
                    select = new Layer_Document();
                    break;
                case R.id.report:
                    select = new Layer_Report();
                    break;
                case R.id.profile:
                    select = new Layer_Profile();
                    break;
            }
            assert select != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, select).commit();
            return true;
        });
    }


}
