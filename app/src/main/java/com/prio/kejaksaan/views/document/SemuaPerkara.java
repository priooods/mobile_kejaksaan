package com.prio.kejaksaan.views.document;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prio.kejaksaan.adapter.AdapterPerkara;
import com.prio.kejaksaan.adapter.AdapterSurat;
import com.prio.kejaksaan.databinding.FragDocumentListBinding;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;

import java.util.List;

public class SemuaPerkara extends Fragment {

    FragDocumentListBinding binding;
    AdapterPerkara adapterSurat;
    List<PerkaraListModel.Item> list;

    public SemuaPerkara(PerkaraListModel perkara){
        list = perkara.data;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragDocumentListBinding.inflate(inflater,container,false);

        binding.desc.setText("Tap item to upload Letter status");
        switch (UserModel.i.type){
            case "Jurusita":
            case "Panmud":
            case "PPK":
                storeAdapter();
                break;
        }

        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void storeAdapter() {
        adapterSurat = new AdapterPerkara(list ,requireContext());
        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listDocument.setHasFixedSize(true);
        binding.listDocument.setAdapter(adapterSurat);
    }

}
