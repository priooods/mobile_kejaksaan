package com.prio.kejaksaan.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ActivityBaseBinding;
import com.prio.kejaksaan.layer.Layer_Document;
import com.prio.kejaksaan.layer.Layer_Home;
import com.prio.kejaksaan.layer.Layer_Profile;
import com.prio.kejaksaan.layer.Layer_Report;

public class BaseActivity extends AppCompatActivity {

    ActivityBaseBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new Layer_Home()).commit();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
    }


}
