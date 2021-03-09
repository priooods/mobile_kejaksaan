package com.prio.kejaksaan.layer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.prio.kejaksaan.databinding.FragProfileBinding;
import com.prio.kejaksaan.tools.PagerAdapter;
import com.prio.kejaksaan.views.UsersList;

import java.util.Objects;

public class Layer_Profile extends Fragment {

    FragProfileBinding binding;
    PagerAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragProfileBinding.inflate(inflater, container, false);


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
        return binding.getRoot();
    }
}
