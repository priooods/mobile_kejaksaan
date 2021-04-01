package com.prio.kejaksaan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ModelPerkaraBinding;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.SuratModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.views.document.AddDocument;
import com.prio.kejaksaan.views.perkara.DetailPerkara;

import java.util.List;

public class AdapterSuratList extends RecyclerView.Adapter<AdapterSuratList.vHolder> {

    Context context;
    List<SuratModel.Item> models;

    public AdapterSuratList(Context context, List<SuratModel.Item> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull vHolder holder, int position) {
        holder.binding.namaTerdakwa.setText(models.get(position).tipe);
        holder.binding.dakwaan.setText(models.get(position).perkara.getIdentity());
        holder.binding.garis.setBackground(context.getResources().getDrawable(R.color.red));
        holder.binding.t1.setText("Nomor");
        holder.binding.t2.setText("Pengantar");
        holder.binding.t3.setText("Tanggal");
        holder.binding.t4.setText("Verifikasi");
        holder.binding.t5.setText("Jenis");
        holder.binding.t6.setVisibility(View.GONE);//setText("Dakwaan");


        holder.binding.v1.setText(models.get(position).perkara.nomor);
        if (models.get(position).daftar_pengantar == null) {
            holder.binding.v2.setText("Belum");
            holder.binding.v2.setTextColor(context.getColor(R.color.red));
        } else {
            holder.binding.v2.setText("Sudah");
            holder.binding.v2.setTextColor(context.getColor(R.color.green));
        }
        holder.binding.v3.setText(models.get(position).perkara.tanggal);
        if (models.get(position).verifier_id == null) {
            holder.binding.v4.setText("Belum");
            holder.binding.v4.setTextColor(context.getColor(R.color.red));
        } else {
            holder.binding.v4.setText("Sudah");
            holder.binding.v4.setTextColor(context.getColor(R.color.green));
        }
        holder.binding.v5.setText(models.get(position).perkara.jenis);
        holder.binding.v6.setVisibility(View.GONE);//setText(models.get(position).perkara.jenis);
        holder.binding.vwmodel.setOnClickListener(v -> {
//                    SuratModel.i = models.get(position);
            FragmentActivity frg = (FragmentActivity) (context);
            FragmentManager mrg = frg.getSupportFragmentManager();
            DialogFragment fragment = null;
            switch (UserModel.i.type) {
                case "Panmud":
                    fragment = new AddDocument(2, models.get(position));
                    break;
                case "Jurusita":
                    fragment = new AddDocument(3, models.get(position));
                    break;
                case "PPK":
                    if (models.get(position).verifier_id == null)
                        fragment = new AddDocument(4, models.get(position));
                    else
                        fragment = new AddDocument(5, models.get(position));
                    break;
            }
            fragment.show(mrg, "Detail Surat");
        });
    }

    @Override
    public int getItemCount() {
        if (models != null){
            return models.size();
        } else
            return 0;
    }

    public static class vHolder extends RecyclerView.ViewHolder{
        ModelPerkaraBinding binding;
        public vHolder(ModelPerkaraBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
