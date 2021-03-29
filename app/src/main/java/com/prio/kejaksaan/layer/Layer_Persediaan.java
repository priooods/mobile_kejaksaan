package com.prio.kejaksaan.layer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import com.prio.kejaksaan.adapter.AdapterAtk;
import com.prio.kejaksaan.databinding.FragPersedianBinding;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.views.atk.AddATK;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Persediaan extends Fragment {

    FragPersedianBinding binding;
    AdapterAtk adapterAtk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragPersedianBinding.inflate(inflater,container,false);

        binding.shimer.startShimmer();
        GetAllAtk();

        binding.btnAddTop.setOnClickListener(v -> {
            switch (UserModel.i.type){
                case "PP":
                    AtkModel.StatusAddATK = 1;
                    break;
                case "Pengelola Persediaan":
                    AtkModel.StatusAddATK = 0;
                    break;
            }
            DialogFragment dialogFragment = new AddATK();
            dialogFragment.show(requireActivity().getSupportFragmentManager(),"Add ATK");
        });

        return binding.getRoot();
    }


    public void GetAllAtk(){
        Call<List<AtkModel>> call = BaseModel.i.getService().AllAtk();
        call.enqueue(new Callback<List<AtkModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<AtkModel>> call, @NotNull Response<List<AtkModel>> response) {
                List<AtkModel> data = response.body();
                AtkModel.atklist = data;
                adapterAtk = new AdapterAtk(requireContext(),data);
                binding.listPersediaan.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                binding.listPersediaan.setHasFixedSize(true);
                binding.listPersediaan.setAdapter(adapterAtk);
                binding.shimer.stopShimmer();
                binding.shimer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<List<AtkModel>> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

}
