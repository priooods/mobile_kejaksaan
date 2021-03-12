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
import com.prio.kejaksaan.databinding.FragProfileBinding;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.PagerAdapter;
import com.prio.kejaksaan.views.UsersList;
import com.prio.kejaksaan.views.profile.CreateUsers;
import com.prio.kejaksaan.views.profile.EditProfile;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Profile extends Fragment {

    FragProfileBinding binding;
    PagerAdapter adapter;
    SharedPreferences sharedPreferences;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragProfileBinding.inflate(inflater, container, false);

        binding.username.setText(UserModel.i.fullname);
        binding.access.setText(UserModel.i.type);
        if (UserModel.i.avatar != null){
            Glide.with(this).load("https://digitalsystemindo.com/jaksa/public/images/" + UserModel.i.avatar)
                    .circleCrop().into(binding.profileImage);
        }
        sharedPreferences = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        binding.tabMenu.setupWithViewPager(binding.viewpager);
        adapter = new PagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new UsersList(), "User Access");
        adapter.AddFragment(new UsersList(), "Information");
        adapter.AddFragment(new UsersList(), "My Document");
        adapter.AddFragment(new UsersList(), "History");
        binding.viewpager.setAdapter(adapter);
        for(int i=0; i < binding.tabMenu.getTabCount(); i++) {
            View tab = ((ViewGroup) binding.tabMenu.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(30, 0, 10, 0);
            tab.requestLayout();
        }
        binding.btnCreateUsers.setOnClickListener(V -> {
            DialogFragment dialogFragment = new CreateUsers();
            dialogFragment.show(requireActivity().getSupportFragmentManager(),"Create User");
        });
        binding.btnEditProfile.setOnClickListener(V -> {
            DialogFragment dialogFragment = new EditProfile();
            dialogFragment.show(requireActivity().getSupportFragmentManager(),"Edit Profile");
        });

        binding.menuDots.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(requireContext(), binding.menuDots);
            popupMenu.inflate(R.menu.menu_profile);
            try {
                Method method = popupMenu.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                method.setAccessible(true);
                method.invoke(popupMenu.getMenu(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.logout) {
                    Logout();
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
                BaseModel data = (BaseModel) response.body();
                if (Calling.TreatResponse(requireContext(), "logout", data)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
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
}
