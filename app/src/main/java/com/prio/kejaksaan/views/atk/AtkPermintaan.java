package com.prio.kejaksaan.views.atk;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prio.kejaksaan.adapter.AdapterRequestATK;
import com.prio.kejaksaan.databinding.FragAtkReqBinding;
import com.prio.kejaksaan.layer.Layer_Persediaan;
import com.prio.kejaksaan.layer.goFilter;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.AtkRequest;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class AtkPermintaan extends Fragment implements goFilter {

    FragAtkReqBinding binding;
    AdapterRequestATK adapterSelainLogistik;
    int ID = new Random().nextInt(1000);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragAtkReqBinding.inflate(inflater, container, false);
        binding.shimer.startShimmer();
        Log.e("ATKFILTER", ID+" begin Call!");
        switch (UserModel.i.type) {
            case "Pengelola Persediaan":
                GetATKRequest(BaseModel.i.getService().ATkreqLog(BaseModel.i.token));
                break;
            case "PP":
                GetATKRequest(BaseModel.i.getService().ATkreqPP(BaseModel.i.token));
                break;
            case "PPK":
                GetATKRequest(BaseModel.i.getService().ATkreqPPK(BaseModel.i.token));
                break;
        }

        return binding.getRoot();
    }

    public void GetATKRequest(Call<AtkRequest> call) {
        call.enqueue(new Callback<AtkRequest>() {
            @Override
            public void onResponse(@NotNull Call<AtkRequest> call, @NotNull Response<AtkRequest> response) {
                AtkRequest atkModel = response.body();
                if (Calling.TreatResponse(getContext(), "req atk Log", atkModel)) {
                    assert atkModel != null;
                    adapterSelainLogistik = new AdapterRequestATK(getContext(), atkModel.data);
                    Log.e("ATKFILTER", ID+" on Call adapterSelainLogistik refreshed "+atkModel.data.size()+"item!");
                    binding.listAtkReq.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                    binding.listAtkReq.setAdapter(adapterSelainLogistik);
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }else
                    Log.e("ATKFILTER", ID+" on Call adapterSelainLogistik fail refresh!"+atkModel.data.size());
            }

            @Override
            public void onFailure(@NotNull Call<AtkRequest> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public void Filter(CharSequence filters) {
        if (adapterSelainLogistik != null)
            adapterSelainLogistik.getFilter().filter(filters);

            Log.e("ATKFILTER", ID+" adapterSelainLogistik == "+(adapterSelainLogistik != null ? "EXISTS":"NULL")+"!");
    }

    @Override
    public int getID() {
        return ID;
    }
}
