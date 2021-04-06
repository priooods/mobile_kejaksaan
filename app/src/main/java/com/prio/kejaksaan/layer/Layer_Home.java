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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.prio.kejaksaan.R;
import com.prio.kejaksaan.activity.Login;
import com.prio.kejaksaan.adapter.AdapterAllUsers;
import com.prio.kejaksaan.adapter.AdapterNotif;
import com.prio.kejaksaan.adapter.AdapterPerkara;
import com.prio.kejaksaan.adapter.AdapterPerkaraVerif;
import com.prio.kejaksaan.databinding.FragHomeBinding;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.ModelNotification;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.views.profile.CreateUsers;
import com.prio.kejaksaan.views.profile.EditProfile;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Home extends Fragment {

    FragHomeBinding binding;
    SharedPreferences sharedPreferences;
    AdapterNotif adapterNotif;
    AdapterAllUsers adapterAllUsers;
    List<PerkaraModel> modelList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragHomeBinding.inflate(inflater, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        binding.textUcapan.setText(getTimeZoneText(sharedPreferences.getString("name",null)));
        binding.shimer.startShimmer();
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
            //TODO: Dicheck kalau typenya not KPA.kalau ga berhasil kabarin lgi
            if (UserModel.i.type.equals("KPA") || UserModel.i.type.equals("SuperUser")){
                popupMenu.getMenu().findItem(R.id.add).setVisible(true);
            } else{
                popupMenu.getMenu().findItem(R.id.add).setVisible(false);
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
                        UserModel.TypeCreateUser = 80;
                        DialogFragment fragment = new EditProfile();
                        fragment.show(requireActivity().getSupportFragmentManager(),"Edit Profile");
                        break;
                }
                return false;
            });
            popupMenu.show();
        });

        //TODO: Nunggu API Notif
        binding.shimer.setVisibility(View.GONE);
        binding.layoutKosong.setVisibility(View.VISIBLE);

        switch (Objects.requireNonNull(sharedPreferences.getString("type", null))) {
            case "KPA":
            case "SuperUser":
            case "Ketua":
                binding.layoutKosong.setVisibility(View.GONE);
                binding.listNotification.setVisibility(View.GONE);
                binding.titleKpa.setVisibility(View.VISIBLE);
                binding.listUserss.setVisibility(View.VISIBLE);
                GettingUserAll();
                break;
            default:
                binding.listNotification.setVisibility(View.VISIBLE);
                binding.listUserss.setVisibility(View.GONE);
                ShowingNotif();
                break;

        }
        return binding.getRoot();
    }

    public void GettingNotifPPK(){
        Call<AtkModel> call = BaseModel.i.getService().AtkNotifUntukPPK(BaseModel.i.token);
        call.enqueue(new Callback<AtkModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
                AtkModel atkModel = response.body();
                if(Calling.TreatResponse(getContext(),"req atk pp", atkModel)){
                    assert atkModel != null;
                    if (atkModel.data.size() == 0){
                        binding.layoutKosong.setVisibility(View.VISIBLE);
                    }
                    NotifikasiPPK(atkModel.data);
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void NotifikasiPPK(List<AtkModel> md){
        AdapterPerkaraVerif adapterPerkaraVerif = new AdapterPerkaraVerif(requireContext(),md);
        binding.listNotification.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listNotification.setAdapter(adapterPerkaraVerif);
        binding.shimer.stopShimmer();
        binding.shimer.setVisibility(View.GONE);
    }

    public void ListNotif(List<ModelNotification.Item> md){
        adapterNotif = new AdapterNotif(requireContext(),md);
        binding.listNotification.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.listNotification.setAdapter(adapterNotif);
        adapterNotif.notifyDataSetChanged();
        binding.shimer.stopShimmer();
        binding.shimer.setVisibility(View.GONE);
    }

    public void Logout(){
        Call<BaseModel> call = BaseModel.i.getService().Logout(BaseModel.i.token);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                BaseModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "logout", data)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token");
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

    public void ListUsers(List<UserModel> md){
        if (getContext() == null)
            return;
        adapterAllUsers = new AdapterAllUsers(md,requireContext());
        binding.listUserss.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.listUserss.setAdapter(adapterAllUsers);
        adapterAllUsers.notifyDataSetChanged();
        binding.shimer.stopShimmer();
        binding.shimer.setVisibility(View.GONE);
    }

    public void GettingUserAll(){
        Call<List<UserModel>> call = BaseModel.i.getService().FilterUser(BaseModel.i.token,"type");
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<UserModel>> call, @NotNull Response<List<UserModel>> response) {
                List<UserModel> mode = response.body();
                ListUsers(mode);
                Log.i(TAG, "onResponse: " + mode);
            }

            @Override
            public void onFailure(@NotNull Call<List<UserModel>> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
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

    public void ShowingNotif(){
        Call<ModelNotification> call = BaseModel.i.getService().AllNotifikasiUser(BaseModel.i.token);
        call.enqueue(new Callback<ModelNotification>() {
            @Override
            public void onResponse(@NotNull Call<ModelNotification> call, @NotNull Response<ModelNotification> response) {
                ModelNotification mode = response.body();
                if (Calling.TreatResponse(getContext(),"Getting Notifikasi", mode)){
                    if (mode != null && mode.data.size() == 0){
                        binding.layoutKosong.setVisibility(View.VISIBLE);
                    } else {
                        assert mode != null;
                        Log.i(TAG, "onResponse: " + mode);
                        adapterNotif = new AdapterNotif(getContext(),mode.data);
                        binding.listNotification.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                        binding.listNotification.setAdapter(adapterNotif);
                        adapterNotif.notifyDataSetChanged();
                        binding.shimer.stopShimmer();
                        binding.shimer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ModelNotification> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}
