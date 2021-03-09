package com.prio.kejaksaan.layer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prio.kejaksaan.databinding.FragHomeBinding;

public class Layer_Home extends Fragment {

    FragHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}
