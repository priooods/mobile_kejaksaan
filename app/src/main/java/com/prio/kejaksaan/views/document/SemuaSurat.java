package com.prio.kejaksaan.views.document;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prio.kejaksaan.adapter.AdapterSurat;
import com.prio.kejaksaan.adapter.AdapterSuratList;
import com.prio.kejaksaan.databinding.FragDocumentListBinding;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;

import java.util.List;

public class SemuaSurat extends Fragment {

    FragDocumentListBinding binding;
    AdapterSuratList adapterSurat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragDocumentListBinding.inflate(inflater, container, false);

        binding.desc.setText("Tap item to show documents");
        switch (UserModel.i.type){
            case "Jurusita":
                storeAdapterJurusita(DocumentModel.semuaTugasJurusita);
                break;
            case "Panmud":
                storeAdapterPanmud(DocumentModel.semuasuratPanmud);
                break;
            case "PPK":
                storeAdapterJPPK(DocumentModel.semuaTugasPPK);
                break;
        }
        return binding.getRoot();
    }

    public void storeAdapterPanmud(List<DocumentModel> md){
        adapterSurat = new AdapterSuratList(requireContext(), md);
        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listDocument.setHasFixedSize(true);
        binding.listDocument.setAdapter(adapterSurat);
    }

    public void storeAdapterJurusita(List<DocumentModel> md){
        adapterSurat = new AdapterSuratList(requireContext(), md);
        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listDocument.setHasFixedSize(true);
        binding.listDocument.setAdapter(adapterSurat);
    }

    public void storeAdapterJPPK(List<DocumentModel> md){
        adapterSurat = new AdapterSuratList(requireContext(), md);
        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listDocument.setHasFixedSize(true);
        binding.listDocument.setAdapter(adapterSurat);
    }
}