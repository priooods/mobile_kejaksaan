package com.prio.kejaksaan.layer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prio.kejaksaan.databinding.FragReportBinding;
import com.prio.kejaksaan.tools.PagerAdapter;
import com.prio.kejaksaan.views.report.Persediaan;

public class Layer_Report extends Fragment {

    FragReportBinding binding;
    PagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding =  FragReportBinding.inflate(inflater, container, false);

        binding.tablayout.setupWithViewPager(binding.viewpager);
        adapter = new PagerAdapter(getChildFragmentManager());
        adapter.AddFragment(new Persediaan(), "Anggaran");
        adapter.AddFragment(new Persediaan(), "Persediaan");
        adapter.AddFragment(new Persediaan(), "Perkara");
        binding.viewpager.setAdapter(adapter);

        return binding.getRoot();
    }
}
