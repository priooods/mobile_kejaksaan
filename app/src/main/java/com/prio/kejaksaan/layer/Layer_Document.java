package com.prio.kejaksaan.layer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prio.kejaksaan.databinding.FragDocumentBinding;

public class Layer_Document extends Fragment {

    FragDocumentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragDocumentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}
