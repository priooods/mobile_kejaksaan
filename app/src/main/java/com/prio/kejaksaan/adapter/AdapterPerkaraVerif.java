package com.prio.kejaksaan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ModelAtkReqBinding;
import com.prio.kejaksaan.databinding.ModelPerkaraBinding;
import com.prio.kejaksaan.layer.Layer_Persediaan;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class AdapterPerkaraVerif extends RecyclerView.Adapter<AdapterPerkaraVerif.vHolder> {

    Context context;
    List<AtkModel> models;

    public AdapterPerkaraVerif(Context context, List<AtkModel> atkModels) {
        this.context = context;
        this.models = atkModels;
    }

    @NonNull
    @Override
    public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull vHolder holder, int position) {
        switch (UserModel.i.type){
            case "PPK":
                if (models.get(position).ppk_id != 0){
                    holder.binding.namaTerdakwa.setVisibility(View.GONE);
                } else {
                    holder.binding.namaTerdakwa.setText("Verifikasi");
                    holder.binding.namaTerdakwa.setGravity(Gravity.END);
                    holder.binding.namaTerdakwa.setTextColor(context.getColor(R.color.colorPrimaryDark));
                    holder.binding.namaTerdakwa.setOnClickListener(v -> GetATkPPKVerify(models.get(position).id));
                }
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

    public static class vHolder extends RecyclerView.ViewHolder{
        ModelPerkaraBinding binding;
        public vHolder(ModelPerkaraBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }

    public void GetATkPPKVerify(int ids){
        Call<PerkaraModel> call = BaseModel.i.getService().ReqATKPPK(BaseModel.i.token, ids);
        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel atkModel = response.body();
                if(Calling.TreatResponse(context,"req atk pp", atkModel)){
                    assert atkModel != null;
                    MDToast.makeText(context, "Request Berhasil di verifikasi ! Refresh Halaman anda", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
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
