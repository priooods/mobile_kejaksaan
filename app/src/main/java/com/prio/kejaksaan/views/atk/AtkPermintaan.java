package com.prio.kejaksaan.views.atk;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.FragAtkReqBinding;
import com.prio.kejaksaan.databinding.ModelAtkReqBinding;
import com.prio.kejaksaan.databinding.ModelPerkaraBinding;
import com.prio.kejaksaan.layer.Layer_Persediaan;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class AtkPermintaan extends Fragment {

    FragAtkReqBinding binding;
    AdapterSelainLogistik adapterSelainLogistik;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragAtkReqBinding.inflate(inflater, container, false);
        binding.shimer.startShimmer();
        switch (UserModel.i.type){
            case "Pengelola Persediaan":
                GetATkLog();
                break;
            case "PP":
                GetATkPP();
                break;
        }

        return binding.getRoot();
    }

    public void GetATkLog(){
        Call<AtkModel> call = BaseModel.i.getService().ATkreqLog(BaseModel.i.token);
        call.enqueue(new Callback<AtkModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
                AtkModel atkModel = response.body();
                if(Calling.TreatResponse(requireContext(),"req atk Log", atkModel)){
                    assert atkModel != null;
                    adapterSelainLogistik = new AdapterSelainLogistik(requireContext(), atkModel.data);
                    binding.listAtkReq.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
                    binding.listAtkReq.setAdapter(adapterSelainLogistik);
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void GetATkPP(){
        Call<AtkModel> call = BaseModel.i.getService().ATkreqPP(BaseModel.i.token);
        call.enqueue(new Callback<AtkModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
                AtkModel atkModel = response.body();
                if(Calling.TreatResponse(requireContext(),"req atk pp", atkModel)){
                    assert atkModel != null;
                    adapterSelainLogistik = new AdapterSelainLogistik(requireContext(), atkModel.data);
                    binding.listAtkReq.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
                    binding.listAtkReq.setAdapter(adapterSelainLogistik);
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public class AdapterSelainLogistik extends RecyclerView.Adapter<AdapterSelainLogistik.vHolder>{

        Context context;
        List<AtkModel> models;

        public AdapterSelainLogistik(Context context, List<AtkModel> models) {
            this.context = context;
            this.models = models;
        }

        @NonNull
        @Override
        public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new vHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull vHolder holder, int position) {
            switch (UserModel.i.type){
                case "PP":
                    holder.binding.namaTerdakwa.setVisibility(View.GONE);
                    break;
                case "Pengelola Persediaan":
                    holder.binding.namaTerdakwa.setVisibility(View.GONE);
                    holder.binding.card.setOnClickListener(v -> {
                        AtkModel.StatusAddATK = 2;
                        AtkModel.i = models.get(position);
                        DialogFragment dialogFragment = new AddATK();
                        dialogFragment.show(requireActivity().getSupportFragmentManager(),"Add ATK");
                    });
                    break;
            }

            holder.binding.dakwaan.setVisibility(View.GONE);
            holder.binding.t1.setText("PPK");
            holder.binding.t2.setText("Logistik");
            if (models.get(position).ppk_id != 0){
                holder.binding.v1.setText("Verified");
                holder.binding.v1.setTextColor(context.getColor(R.color.green));
            } else {
                holder.binding.v1.setText("Unverified");
                holder.binding.v1.setTextColor(context.getColor(R.color.red));
            }

            if (models.get(position).log_id != 0){
                holder.binding.v2.setText("Verified");
                holder.binding.v2.setTextColor(context.getColor(R.color.green));
            } else {
                holder.binding.v2.setText("Unverified");
                holder.binding.v2.setTextColor(context.getColor(R.color.red));
            }

            holder.binding.l3.setVisibility(View.GONE);
            holder.binding.l4.setVisibility(View.GONE);
            holder.binding.l5.setVisibility(View.GONE);
            holder.binding.l6.setVisibility(View.GONE);

            AdapterAnak adapterAnak = new AdapterAnak(context, models.get(position).barang);
            holder.binding.listOnmodel.setVisibility(View.VISIBLE);
            holder.binding.listOnmodel.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
            holder.binding.listOnmodel.setAdapter(adapterAnak);
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        public class vHolder extends RecyclerView.ViewHolder{
            ModelPerkaraBinding binding;
            public vHolder(ModelPerkaraBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }
        }
    }

    public static class AdapterAnak extends RecyclerView.Adapter<AdapterAnak.vHolder>{

        Context context;
        List<AtkModel> models;

        public AdapterAnak(Context context, List<AtkModel> models) {
            this.context = context;
            this.models = models;
        }

        @NonNull
        @Override
        public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new vHolder(ModelAtkReqBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull vHolder holder, int position) {
            holder.binding.r1.setVisibility(View.GONE);
            holder.binding.r2.setVisibility(View.VISIBLE);
            holder.binding.nameDetail1.setText("Nama : " + models.get(position).name);
            holder.binding.jumlahDetail1.setText("Jumlah : " + String.valueOf(models.get(position).jumlah) + " Items");
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        public static class vHolder extends RecyclerView.ViewHolder{
            ModelAtkReqBinding binding;
            public vHolder(ModelAtkReqBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }
        }
    }
}
