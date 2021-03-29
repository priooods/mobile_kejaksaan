package com.prio.kejaksaan.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.adapter.AdapterAllUsers;
import com.prio.kejaksaan.databinding.FragUserslistBinding;
import com.prio.kejaksaan.model.UserModel;

public class UsersList extends Fragment {

    FragUserslistBinding binding;
    AdapterAllUsers adapterAllUsers;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragUserslistBinding.inflate(inflater,container,false);

//        adapterAllUsers = new AdapterAllUsers(UserModel.gettingDataAllUsers, requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.listUsers.setLayoutManager(layoutManager);
        binding.listUsers.setAdapter(adapterAllUsers);
        return binding.getRoot();
    }
}
