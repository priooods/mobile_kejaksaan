package com.prio.kejaksaan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.databinding.ModelAnakPerkaraBinding;
import com.prio.kejaksaan.model.PerkaraListModel;

import java.util.List;

public class AdapterAnakPerkara extends RecyclerView.Adapter<AdapterAnakPerkara.vholders> {

    Context context;
    List<PerkaraListModel.Proses> model;

    public AdapterAnakPerkara(Context context, List<PerkaraListModel.Proses> model) {
        this.context = context;
        this.model = model;
    }

    @NonNull
    @Override
    public vholders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vholders(ModelAnakPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull vholders holder, int position) {
        holder.binding.agenda.setText(model.get(position).agenda);
        holder.binding.tanggalProsess.setText(model.get(position).tanggal);
        holder.binding.penahanan.setText(model.get(position).penahanan);
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public static class vholders extends RecyclerView.ViewHolder{
        ModelAnakPerkaraBinding binding;
        public vholders(ModelAnakPerkaraBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
