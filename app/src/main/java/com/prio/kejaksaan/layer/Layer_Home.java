package com.prio.kejaksaan.layer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prio.kejaksaan.activity.BaseActivity;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragHomeBinding.inflate(inflater, container, false);

        GettingDetail();
        return binding.getRoot();
    }

    private String getTimeZoneText(UserModel userData){
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        int hours = c.get(Calendar.HOUR_OF_DAY);

        if(hours>=4 && hours<=12){
            return "Good Morning "+ userData.fullname +"!";
        }else if(hours>=12 && hours<=17){
            return "Good Afternoon "+ userData.fullname +"!";
        }else{
            return "Good Evening "+ userData.fullname +"!";
        }
    }

    public void GettingDetail(){
        Call<BaseModel> call = BaseModel.i.getService().UserDetail(BaseModel.i.token);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                BaseModel data = (BaseModel) response.body();
                if (Calling.TreatResponse(requireContext(), "profile", data)) {
                    assert data != null;
                    UserModel.i = data.data;
                    binding.textUcapan.setText(getTimeZoneText(UserModel.i));
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}
