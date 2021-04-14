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
import com.prio.kejaksaan.databinding.FragDocumentListBinding;
import com.prio.kejaksaan.layer.goFilter;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.UserModel;

import java.util.List;

public class SemuaPerkara extends Fragment implements goFilter {

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

        switch (UserModel.i.type){
            case "Jurusita":
            case "PPK":
                binding.desc.setText("Pilih salah satu untuk melihat rincian");
                break;
            case "Panmud":
                binding.desc.setText("Pilih salah satu perkara untuk dibuatkan surat");
                break;
        }
        storeAdapter();

        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void storeAdapter() {
        adapterSurat = new AdapterPerkara(list ,requireContext(), false);
        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listDocument.setHasFixedSize(true);
        binding.listDocument.setAdapter(adapterSurat);
    }

    @Override
    public void Filter(CharSequence filters) {
        if (adapterSurat != null)
            adapterSurat.getFilter().filter(filters);
    }

    @Override
    public int getID() {
        return 0;
    }
}
