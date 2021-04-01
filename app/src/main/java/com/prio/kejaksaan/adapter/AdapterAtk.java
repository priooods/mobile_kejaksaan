package com.prio.kejaksaan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.databinding.ModelAtkBinding;
import com.prio.kejaksaan.model.AtkItemModel;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.UserModel;

import java.util.List;

public class AdapterAtk extends RecyclerView.Adapter<AdapterAtk.vHolder> {

    Context context;
    List<AtkItemModel.Item> models;

    public AdapterAtk(Context context, List<AtkItemModel.Item> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vHolder(ModelAtkBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull vHolder holder, int position) {
//        holder.binding.name.setText("default_name "+position);
//        holder.binding.ket.setText("default_keterangan "+position);
//        holder.binding.jumlah.setText("default_jumlah "+position + " Items");
        holder.binding.name.setText(models.get(position).name);
        holder.binding.ket.setText(models.get(position).keterangan);
        holder.binding.jumlah.setText(models.get(position).jumlah + " Items");
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class vHolder extends RecyclerView.ViewHolder{
        ModelAtkBinding binding;
        public vHolder(ModelAtkBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
