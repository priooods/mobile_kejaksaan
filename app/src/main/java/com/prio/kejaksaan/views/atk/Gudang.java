package com.prio.kejaksaan.views.atk;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prio.kejaksaan.adapter.AdapterAtk;
import com.prio.kejaksaan.databinding.FragAtkReqBinding;
import com.prio.kejaksaan.layer.goFilter;
import com.prio.kejaksaan.model.AtkItemModel;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.service.Calling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Gudang extends Fragment implements goFilter {

    FragAtkReqBinding binding;
    AdapterAtk adapterAtk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragAtkReqBinding.inflate(inflater, container, false);

        binding.desc.setText("Show all ATK in your repository");
        GetAllAtk();
        return binding.getRoot();
    }

    public void GetAllAtk(){
        Call<AtkItemModel> call = BaseModel.i.getService().AllAtk();
        call.enqueue(new Callback<AtkItemModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkItemModel> call, @NotNull Response<AtkItemModel> response) {
                AtkItemModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "list_atk", data)) {
                    if (data.data.size() >0) {
                        adapterAtk = new AdapterAtk(requireContext(), data.data);
                        binding.listAtkReq.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                        binding.listAtkReq.setAdapter(adapterAtk);
                    }
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkItemModel> call, @NotNull Throwable t) {
                Log.e("TAG", "onFailure: ", t);
            }
        });
    }

    @Override
    public void Filter(CharSequence filters) {
        if (adapterAtk != null)
            adapterAtk.getFilter().filter(filters);
    }

    @Override
    public int getID() {
        return 0;
    }
}
