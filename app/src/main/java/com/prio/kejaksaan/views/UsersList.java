package com.prio.kejaksaan.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prio.kejaksaan.databinding.FragUserslistBinding;

public class UsersList extends Fragment {

    FragUserslistBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragUserslistBinding.inflate(inflater,container,false);

        Log.d("Users", "Ada nih");

        return binding.getRoot();
    }
}
