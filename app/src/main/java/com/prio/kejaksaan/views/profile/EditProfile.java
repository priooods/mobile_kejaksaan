package com.prio.kejaksaan.views.profile;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.DialogEditProfileBinding;
import com.prio.kejaksaan.model.UserModel;

public class EditProfile extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullscreen);
    }

    DialogEditProfileBinding binding;
    String[] listType = { "SuperUser","Ketua","Panitera","KPA","Panmud","PP","Jurusita","PPK","Bendahara","Pengelola Persediaan"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEditProfileBinding.inflate(inflater,container,false);

        if (UserModel.i.avatar != null) {
            Glide.with(this).load("https://digitalsystemindo.com/jaksa/public/images/" + UserModel.i.avatar)
                    .circleCrop().into(binding.avatar);
        }
        binding.name.setText(UserModel.i.name);
        binding.fullname.setText(UserModel.i.fullname);
        binding.password.setText(UserModel.i.password);
        binding.backpress.setOnClickListener(v->dismiss());
        if (!UserModel.i.type.equals("SuperUser")){
            binding.typeLayout.setVisibility(View.GONE);
        }
        binding.type.setText(UserModel.i.type);

        binding.type.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, listType);
        binding.type.setAdapter(adapter);


        return binding.getRoot();
    }
}
