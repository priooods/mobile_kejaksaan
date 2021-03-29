package com.prio.kejaksaan.layer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.prio.kejaksaan.databinding.FragHomeBinding;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Home extends Fragment {

    FragHomeBinding binding;
    SharedPreferences sharedPreferences;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragHomeBinding.inflate(inflater, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        binding.textUcapan.setText(getTimeZoneText(sharedPreferences.getString("name",null)));
        return binding.getRoot();
    }

    private String getTimeZoneText(String fullname){
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        int hours = c.get(Calendar.HOUR_OF_DAY);

        if(hours>=4 && hours<=12){
            return "Good Morning "+ fullname +"!";
        }else if(hours>=12 && hours<=17){
            return "Good Afternoon "+ fullname +"!";
        }else{
            return "Good Evening "+ fullname +"!";
        }
    }
}
