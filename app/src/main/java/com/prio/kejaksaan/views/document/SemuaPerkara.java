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

import com.prio.kejaksaan.adapter.AdapterSurat;
import com.prio.kejaksaan.databinding.FragDocumentListBinding;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;

import java.util.List;

public class SemuaPerkara extends Fragment {

    FragDocumentListBinding binding;
    AdapterSurat adapterSurat;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragDocumentListBinding.inflate(inflater,container,false);

        binding.desc.setText("Tap item to upload Letter status");
        switch (UserModel.i.type){
            case "Panmud":
                storeAdapter();
                break;
            case "Jurusita":
                storeAdapterJurusita();
                break;
            case "PPK":
                storeAdapterPPK();
                break;
        }

        return binding.getRoot();
    }

    public void storeAdapterPPK(){
        adapterSurat = new AdapterSurat(requireContext(), PerkaraModel.notifyPPK);
        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listDocument.setHasFixedSize(true);
        binding.listDocument.setAdapter(adapterSurat);
    }

    public void storeAdapterJurusita(){
        adapterSurat = new AdapterSurat(requireContext(), PerkaraModel.notifyJurusita);
        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listDocument.setHasFixedSize(true);
        binding.listDocument.setAdapter(adapterSurat);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void storeAdapter(){
        if (DocumentModel.semuasuratPanmud !=null) {
            for (DocumentModel dc : DocumentModel.semuasuratPanmud) {
                PerkaraModel.perkaradiproses.removeIf(ee -> ee.perkara_id == dc.perkara_id);
                adapterSurat = new AdapterSurat(requireContext(), PerkaraModel.perkaradiproses);
                binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
                binding.listDocument.setHasFixedSize(true);
                binding.listDocument.setAdapter(adapterSurat);
            }
        }
    }

}
