package com.prio.kejaksaan.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ModelPerkaraBinding;
import com.prio.kejaksaan.model.AtkRequest;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.tools.Laravel;
import com.prio.kejaksaan.views.atk.AtkVerifikasi;

import java.util.ArrayList;
import java.util.List;

public class AdapterRequestATK extends RecyclerView.Adapter<AdapterRequestATK.vHolder>{

    Context context;
    public List<AtkRequest.Item> models, unfilter;

    public AdapterRequestATK(Context context, List<AtkRequest.Item> models) {
        this.context = context;
        this.models = models;
        this.unfilter = models;
    }

    @NonNull
    @Override
    public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull vHolder holder, int position) {
        holder.binding.namaTerdakwa.setText(models.get(position).proses.getIdentity());
        holder.binding.dakwaan.setText(models.get(position).proses.perkara.getInformation());
        holder.binding.t1.setText("Agenda");
        holder.binding.t2.setText("Diverifikasi");
        holder.binding.t3.setText("Hari");
        holder.binding.t4.setText("Diserahkan");
        holder.binding.t5.setText("Tanggal");
        holder.binding.t6.setText("Diterima");
        holder.binding.v1.setText(models.get(position).proses.agenda);
        if (models.get(position).ppk_id != null){
            holder.binding.v2.setText("Sudah");
            holder.binding.v2.setTextColor(context.getColor(R.color.green));
        } else {
            holder.binding.v2.setText("Belum");
            holder.binding.v2.setTextColor(context.getColor(R.color.red));
        }
        holder.binding.v3.setText(models.get(position).proses.hari);
        if (models.get(position).log_id != null){
            holder.binding.v4.setText("Sudah");
            holder.binding.v4.setTextColor(context.getColor(R.color.green));
        } else
            {
            holder.binding.v4.setText("Belum");
            holder.binding.v4.setTextColor(context.getColor(R.color.red));
        }
        holder.binding.v5.setText(Laravel.getShortDate(models.get(position).proses.tanggal));
        if (models.get(position).penerimaan != null){
            holder.binding.v6.setText("Sudah");
            holder.binding.v6.setTextColor(context.getColor(R.color.green));
        } else {
            holder.binding.v6.setText("Belum");
            holder.binding.v6.setTextColor(context.getColor(R.color.red));
        }
        if (VerifierButton(models.get(position))){
            holder.binding.verifierButton.setVisibility(View.VISIBLE);
            holder.binding.verifierButton.setOnClickListener(view -> {
                VerifierAction(position, models.get(position));
            });
        }else{
            holder.binding.verifierButton.setVisibility(View.GONE);
        }
        AdapterAnakRequestATK adapterAnak = new AdapterAnakRequestATK(context, models.get(position).barang);
        holder.binding.listOnmodel.setVisibility(View.VISIBLE);
        holder.binding.listOnmodel.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
        holder.binding.listOnmodel.setAdapter(adapterAnak);
    }
    public boolean VerifierButton(AtkRequest.Item req){
        return false;
    }
    public void VerifierAction(int position, AtkRequest.Item model){
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
                String key = constraint.toString();
                List<AtkRequest.Item> modelss = new ArrayList<>();
                for (AtkRequest.Item model : unfilter) {
                    PerkaraListModel.Item perkara = model.proses.perkara;
                    if (model.proses.agenda.toLowerCase().contains(key.toLowerCase()) ||
                            perkara.identitas.toLowerCase().contains(key.toLowerCase()) ||
                            perkara.proses.dakwaan.toLowerCase().contains(key.toLowerCase()) ||
                            perkara.tanggal.toLowerCase().contains(key.toLowerCase()) ||
                            perkara.jenis.toLowerCase().contains(key.toLowerCase()) ||
                            perkara.nomor.toLowerCase().contains(key.toLowerCase())) {
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
                models = (List<AtkRequest.Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        if (models == null)
            return 0;
        return models.size();
    }

    public static class vHolder extends RecyclerView.ViewHolder{
        ModelPerkaraBinding binding;
        public vHolder(ModelPerkaraBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
