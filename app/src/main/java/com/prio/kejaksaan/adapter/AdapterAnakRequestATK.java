package com.prio.kejaksaan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.databinding.ModelAtkReqBinding;
import com.prio.kejaksaan.model.AtkItemModel;

import java.util.List;

public class AdapterAnakRequestATK extends RecyclerView.Adapter<AdapterAnakRequestATK.vHolder>{

    Context context;
    List<AtkItemModel.Item> models;

    public AdapterAnakRequestATK(Context context, List<AtkItemModel.Item> models) {
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
