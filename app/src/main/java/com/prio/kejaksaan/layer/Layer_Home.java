package com.prio.kejaksaan.layer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.prio.kejaksaan.R;
import com.prio.kejaksaan.activity.Login;
import com.prio.kejaksaan.databinding.FragHomeBinding;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.views.profile.CreateUsers;
import com.prio.kejaksaan.views.profile.EditProfile;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
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

        binding.usericon.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(requireContext(), binding.usericon);
            popupMenu.inflate(R.menu.menu_profile);
            try {
                Method method = popupMenu.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                method.setAccessible(true);
                method.invoke(popupMenu.getMenu(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }


            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case  R.id.logout:
                        Logout();
                        break;
                    case R.id.add:
                        UserModel.TypeCreateUser = 1;
                        DialogFragment dialogFragment = new CreateUsers();
                        dialogFragment.show(requireActivity().getSupportFragmentManager(),"Create User");
                        break;
                    case R.id.edit:
                        DialogFragment fragment = new EditProfile();
                        fragment.show(requireActivity().getSupportFragmentManager(),"Edit Profile");
                        break;
                }
                return false;
            });
            popupMenu.show();
        });


        return binding.getRoot();
    }

    public void Logout(){
        Call<BaseModel> call = BaseModel.i.getService().Logout(BaseModel.i.token);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                BaseModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "logout", data)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    BaseModel.i.token = "";
                    startActivity(new Intent(requireActivity(), Login.class));
                    requireActivity().finish();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t );
            }
        });
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
