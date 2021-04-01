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
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.views.perkara.DetailPerkara;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class AdapterPerkara extends RecyclerView.Adapter<AdapterPerkara.cHolder> implements Filterable {

    List<PerkaraListModel.Item> models;
    List<PerkaraListModel.Item> modelsfilter;
    Context context;

    public AdapterPerkara(List<PerkaraListModel.Item> models, Context context) {
        this.models = models;
        this.context = context;
        this.modelsfilter = models;
    }

    @NonNull
    @Override
    public cHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new cHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull cHolder holder, int position) {
        SimpleDateFormat fr = new SimpleDateFormat("YYYY/MM/dd", Locale.ENGLISH);
        SimpleDateFormat tex = new SimpleDateFormat("dd MMM YYYY", Locale.ENGLISH);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(Objects.requireNonNull(fr.parse(models.get(position).tanggal)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.binding.v5.setText(": " + "Belum");
        holder.binding.v5.setTextColor(context.getColor(R.color.red));

        if (models.get(position).proses != null){
//            int i;
//            for (i=0; i < PerkaraModel.perkaradiproses.size(); i++){
//                if (models.get(position).id == PerkaraModel.perkaradiproses.get(i).perkara_id) {
//                    if (models.get(position).id != 0) {
                        holder.binding.v5.setText(": Sudah");
                        holder.binding.v5.setTextColor(context.getColor(R.color.green));
                        holder.binding.garis.setBackground(context.getResources().getDrawable(R.color.green));
//                    }
//                }
//            }
        }

        holder.binding.namaTerdakwa.setText(models.get(position).identitas);
        holder.binding.dakwaan.setText(models.get(position).dakwaan);
        holder.binding.l6.setVisibility(View.GONE);
        holder.binding.t1.setText("Nomor");
        holder.binding.t2.setText("Jenis");
        holder.binding.t3.setText("Ditahan");
        holder.binding.t4.setText("Tanggal");
        holder.binding.t5.setText("Status PP");
        holder.binding.v1.setText(": "+models.get(position).nomor);
        holder.binding.v2.setText(": "+models.get(position).jenis);
        holder.binding.v3.setText(": "+models.get(position).penahanan);
        holder.binding.v4.setText(": "+tex.format(c.getTime()));

        holder.binding.vwmodel.setOnClickListener(v -> {
            if (holder.binding.v5.getText().equals(": Sudah")){
                PerkaraModel.statusPerkara = 2;
            } else {
                PerkaraModel.statusPerkara = 1;
            }
            PerkaraListModel.i = models.get(position);
            FragmentActivity frg = (FragmentActivity)(context);
            FragmentManager mrg = frg.getSupportFragmentManager();
            DialogFragment fragment = new DetailPerkara();
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
                                model.penahanan.toLowerCase().contains(key.toLowerCase()) ||
                                model.dakwaan.toLowerCase().contains(key.toLowerCase()) ||
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
