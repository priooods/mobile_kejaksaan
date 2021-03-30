package com.prio.kejaksaan.views.atk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prio.kejaksaan.adapter.AdapterAtk;
import com.prio.kejaksaan.databinding.FragAtkReqBinding;
import com.prio.kejaksaan.model.AtkModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

public class Gudang extends Fragment {

    FragAtkReqBinding binding;
    AdapterAtk adapterAtk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragAtkReqBinding.inflate(inflater, container, false);

        binding.desc.setText("Show all ATK in your repository");
        if (AtkModel.atklist !=null && AtkModel.atklist.size() != 0) {
            adapterAtk = new AdapterAtk(requireContext(), AtkModel.atklist);
            binding.listAtkReq.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            binding.listAtkReq.setAdapter(adapterAtk);
            binding.shimer.stopShimmer();
            binding.shimer.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }
}
