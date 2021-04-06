package com.prio.kejaksaan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.databinding.ModelAtkBinding;
import com.prio.kejaksaan.model.AtkItemModel;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.AtkRequest;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterAtk extends RecyclerView.Adapter<AdapterAtk.vHolder> {

    Context context;
    List<AtkItemModel.Item> models;
    List<AtkItemModel.Item> unfilter;

    public AdapterAtk(Context context, List<AtkItemModel.Item> models) {
        this.context = context;
        this.models = models;
        this.unfilter = models;
    }

    @NonNull
    @Override
    public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vHolder(ModelAtkBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull vHolder holder, int position) {
        holder.binding.name.setText(models.get(position).name);
        holder.binding.ket.setText(models.get(position).keterangan);
        holder.binding.jumlah.setText(models.get(position).jumlah + " Items");
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint.length() == 0){
                    Log.e("Request","No Adapter");
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = unfilter;
                    return filterResults;
                }
                String key = constraint.toString().toLowerCase();
                List<AtkItemModel.Item> modelss = new ArrayList<>();
                for (AtkItemModel.Item model : unfilter) {
                    if (model.name.toLowerCase().contains(key) ||
                            model.keterangan.toLowerCase().contains(key) ||
                            String.valueOf(model.jumlah).contains(key)){
                        modelss.add(model);
                    }
                }
//                models = modelss;

                FilterResults filterResults = new FilterResults();
                filterResults.values = modelss;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                models = (List<AtkItemModel.Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class vHolder extends RecyclerView.ViewHolder{
        ModelAtkBinding binding;
        public vHolder(ModelAtkBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
