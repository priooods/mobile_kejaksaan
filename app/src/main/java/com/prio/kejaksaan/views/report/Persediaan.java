package com.prio.kejaksaan.views.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prio.kejaksaan.databinding.FragPersedianBinding;

public class Persediaan extends Fragment {

    FragPersedianBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragPersedianBinding.inflate(inflater,container, false);


        return binding.getRoot();
    }
}
