package com.prio.kejaksaan.layer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prio.kejaksaan.adapter.AdapterPerkara;
import com.prio.kejaksaan.adapter.AdapterSurat;
import com.prio.kejaksaan.databinding.FragPerkaraBinding;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.views.perkara.AddPerkara;
import com.prio.kejaksaan.views.profile.CreateUsers;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Perkara extends Fragment {

    FragPerkaraBinding binding;
    AdapterPerkara adapterPerkara;
    AdapterSurat adapterSurat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragPerkaraBinding.inflate(inflater, container, false);

        PerkaraModel.updatePerkaraStatus = 0;
        binding.shimer.startShimmer();

        binding.btnNewDoc.setOnClickListener(v -> {
            PerkaraModel.buatPerkaraShow = 1;
            DialogFragment fragment = new AddPerkara();
            fragment.show(requireActivity().getSupportFragmentManager(),"Create Perkara");
        });

        binding.btnSearch.setOnClickListener(v -> {
            binding.search1.setVisibility(View.GONE);
            binding.search2.setVisibility(View.VISIBLE);
        });

        binding.cross.setOnClickListener(v -> {
            binding.search2.setVisibility(View.GONE);
            binding.search1.setVisibility(View.VISIBLE);
            storeAdapter(PerkaraModel.listperkara);
        });

        binding.btnAddTop.setOnClickListener(v -> {
            UserModel.TypeCreateUser = 2;
            DialogFragment dialogFragment = new CreateUsers();
            dialogFragment.show(requireActivity().getSupportFragmentManager(),"Create User");
        });


        binding.findinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() != 0) {
                    adapterPerkara.getFilter().filter(charSequence);
                } else {
                    storeAdapter(PerkaraModel.listperkara);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        return binding.getRoot();
    }

    public void storeAdapter(List<PerkaraModel> md){
        adapterPerkara = new AdapterPerkara(md, requireContext());
        binding.listPerkara.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listPerkara.setHasFixedSize(true);
        binding.listPerkara.setAdapter(adapterPerkara);
    }


    public void storeAdapterPanMud(List<PerkaraModel> md){
        adapterSurat = new AdapterSurat(requireContext(),md);
        binding.listPerkara.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
        binding.listPerkara.setHasFixedSize(true);
        binding.listPerkara.setAdapter(adapterSurat);
    }


    public void GettAllPerkaraPP(){
        Call<PerkaraModel> call = BaseModel.i.getService().PerkaraPP(BaseModel.i.token);
        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel baseModel = response.body();
                if (Calling.TreatResponse(requireContext(), "All Perkara by PP", baseModel)){
                    assert baseModel != null;
                    PerkaraModel.listperkara = baseModel.data.perkara;
                    storeAdapter(PerkaraModel.listperkara);
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t );
            }
        });
    }

    public void GettAllPerkaraJurusita(){
        Call<PerkaraModel> call = BaseModel.i.getService().AllJurusitaPerkara(BaseModel.i.token);
        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel baseModel = response.body();
                if (Calling.TreatResponse(requireContext(), "All Perkara by Jurusita", baseModel)){
                    assert baseModel != null;
                    PerkaraModel.listperkara = baseModel.data.perkara;
                    storeAdapter(PerkaraModel.listperkara);
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t );
            }
        });
    }

    public void GettAllPerkara(){
        Call<List<PerkaraModel>> call = BaseModel.i.getService().AllPerkara();
        call.enqueue(new Callback<List<PerkaraModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<PerkaraModel>> call, @NotNull Response<List<PerkaraModel>> response) {
                PerkaraModel.listperkara = response.body();
                binding.shimer.stopShimmer();
                binding.shimer.setVisibility(View.GONE);
                storeAdapter(PerkaraModel.listperkara);
            }

            @Override
            public void onFailure(@NotNull Call<List<PerkaraModel>> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t );
            }
        });
    }

    public void GetPerkaraSudahDiProsess(){
        Call<PerkaraModel> call = BaseModel.i.getService().PerkaraSudahDiProsess();
        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel baseModel = response.body();
                if (Calling.TreatResponse(requireContext(),"Perkara Sudah Di Prosess", baseModel)){
                    assert baseModel != null;
                    PerkaraModel.perkaradiproses = baseModel.data.perkara;
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        switch (UserModel.i.type){
            case "Jurusita":
                GetPerkaraSudahDiProsess();
                GettAllPerkaraJurusita();
                binding.btnAddTop.setVisibility(View.GONE);
                binding.btnNewDoc.setVisibility(View.GONE);
                break;
            case "Panitera":
            case "SuperUser":
                GetPerkaraSudahDiProsess();
                GettAllPerkara();
                break;
            case "PP":
                GetPerkaraSudahDiProsess();
                GettAllPerkaraPP();
                binding.btnAddTop.setVisibility(View.GONE);
                binding.btnNewDoc.setVisibility(View.GONE);
                break;
        }
    }
}
