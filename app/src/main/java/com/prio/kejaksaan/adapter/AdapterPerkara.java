package com.prio.kejaksaan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ModelPerkaraBinding;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.tools.Laravel;
import com.prio.kejaksaan.views.perkara.DetailPerkara;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdapterPerkara extends RecyclerView.Adapter<AdapterPerkara.cHolder> implements Filterable {

    List<PerkaraListModel.Item> models;
    List<PerkaraListModel.Item> modelsfilter;
    Context context;
    boolean laporan;

    public AdapterPerkara(List<PerkaraListModel.Item> models, Context context, boolean Laporan) {
        this.models = models;
        this.context = context;
        this.modelsfilter = models;
        laporan = Laporan;
    }

    @NonNull
    @Override
    public cHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new cHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull cHolder holder, int position) {

        holder.binding.v2.setText(": " + "Belum");
        holder.binding.v2.setTextColor(context.getColor(R.color.red));
//        Log.e("Adapter Perkara",models.get(position).status);
        if (models.get(position).status.equals("Sudah")) {
            holder.binding.v2.setText(": Sudah");
            holder.binding.v2.setTextColor(context.getColor(R.color.green));
            holder.binding.garis.setBackground(context.getResources().getDrawable(R.color.green));
        }

        holder.binding.namaTerdakwa.setText(models.get(position).identitas);
        holder.binding.dakwaan.setText(models.get(position).nomor);
        holder.binding.l4.setVisibility(View.GONE);
        holder.binding.l5.setVisibility(View.GONE);
        holder.binding.l6.setVisibility(View.GONE);
        holder.binding.t1.setText("Jenis");
        holder.binding.t3.setText("Tanggal");
        holder.binding.t2.setText("Proses");
        holder.binding.v1.setText(": "+models.get(position).jenis);
        holder.binding.v3.setText(": "+ Laravel.getShortDate(models.get(position).tanggal));

        holder.binding.vwmodel.setOnClickListener(v -> {
//            PerkaraListModel.i = models.get(position);
            FragmentActivity frg = (FragmentActivity)(context);
            FragmentManager mrg = frg.getSupportFragmentManager();
            DialogFragment fragment;
            if (laporan){
                fragment = new DetailPerkara(3,models.get(position));
            }else
            if (models.get(position).status.equals("Sudah")){
                fragment = new DetailPerkara(2, models.get(position));
            } else {
                fragment = new DetailPerkara(1, models.get(position));
            }
            fragment.show(mrg,"Detail Perkara");
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String key = constraint.toString();
                if (key.isEmpty()){
                    models = modelsfilter;
                } else {
                    List<PerkaraListModel.Item> modelss = new ArrayList<>();
                    for (PerkaraListModel.Item model : modelsfilter){
                        if (model.identitas.toLowerCase().contains(key.toLowerCase()) ||
                                model.jenis.toLowerCase().contains(key.toLowerCase()) ||
                                model.tanggal.toLowerCase().contains(key.toLowerCase()) ||
                                model.nomor.toLowerCase().contains(key.toLowerCase())){
                            modelss.add(model);
                        }
                    }

                    models = modelss;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = models;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                models = (List<PerkaraListModel.Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class cHolder extends RecyclerView.ViewHolder{
        ModelPerkaraBinding binding;
        public cHolder(ModelPerkaraBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
