package com.prio.kejaksaan.views.document;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prio.kejaksaan.adapter.AdapterSuratList;
import com.prio.kejaksaan.databinding.FragDocumentListBinding;
import com.prio.kejaksaan.layer.goFilter;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.SuratModel;

import java.util.List;

public class SemuaSurat extends Fragment implements goFilter {

    FragDocumentListBinding binding;
    AdapterSuratList adapterSurat;
    List<SuratModel.Item> list;
    public SemuaSurat(SuratModel surat){
        list = surat.data;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragDocumentListBinding.inflate(inflater, container, false);

        binding.desc.setText("Pilih dalam daftar untuk melihat rincian");
//        switch (UserModel.i.type){
//            case "Jurusita":
//                break;
//            case "Panmud":
//                storeAdapter(list);
//                break;
//            case "PPK":
//                storeAdapterJPPK(DocumentModel.semuaTugasPPK);
//                break;
//        }
                storeAdapter(list);
        return binding.getRoot();
    }

    public void storeAdapter(List<SuratModel.Item> md){
        adapterSurat = new AdapterSuratList(requireContext(), md);
        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
//        binding.listDocument.setHasFixedSize(true);
        binding.listDocument.setAdapter(adapterSurat);
    }

    public void storeAdapterJurusita(List<DocumentModel> md){
//        adapterSurat = new AdapterSuratList(requireContext(), md);
//        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
//        binding.listDocument.setHasFixedSize(true);
//        binding.listDocument.setAdapter(adapterSurat);
    }

    public void storeAdapterJPPK(List<DocumentModel> md){
//        adapterSurat = new AdapterSuratList(requireContext(), md);
//        binding.listDocument.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
//        binding.listDocument.setHasFixedSize(true);
//        binding.listDocument.setAdapter(adapterSurat);
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
