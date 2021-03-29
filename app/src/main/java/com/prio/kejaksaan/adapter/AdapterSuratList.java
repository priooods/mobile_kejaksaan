package com.prio.kejaksaan.adapter;

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
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.views.document.AddDocument;
import com.prio.kejaksaan.views.perkara.DetailPerkara;

import java.util.List;

public class AdapterSuratList extends RecyclerView.Adapter<AdapterSuratList.vHolder> {

    Context context;
    List<DocumentModel> models;

    public AdapterSuratList(Context context, List<DocumentModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull vHolder holder, int position) {
        switch (UserModel.i.type){
            case "Panmud":
            case "Jurusita":
                holder.binding.namaTerdakwa.setText(models.get(position).identitas);
                holder.binding.dakwaan.setText(models.get(position).dakwaan);
                holder.binding.garis.setBackground(context.getResources().getDrawable(R.color.red));
                holder.binding.t1.setText("Nomor");
                holder.binding.t2.setText("Pengantar");
                holder.binding.t3.setText("Tanggal");
                holder.binding.t4.setText("Agenda");
                holder.binding.t5.setText("Jenis");
                holder.binding.t6.setText("Dakwaan");


                holder.binding.v2.setText("Sudah");
                holder.binding.v2.setTextColor(context.getColor(R.color.green));
                if (models.get(position).daftar_pengantar == null){
                    holder.binding.v2.setText("Belum");
                    holder.binding.v2.setTextColor(context.getColor(R.color.red));
                }
                holder.binding.v1.setText(models.get(position).nomor);
                holder.binding.v3.setText(models.get(position).tanggal);
                holder.binding.v4.setText(models.get(position).agenda);
                holder.binding.v5.setText(models.get(position).jenis);
                holder.binding.v6.setText(models.get(position).dakwaan);
                holder.binding.vwmodel.setOnClickListener(v -> {
                    DocumentModel.i = models.get(position);
                    DocumentModel.ShowDetailDocument = 2;
                    FragmentActivity frg = (FragmentActivity)(context);
                    FragmentManager mrg = frg.getSupportFragmentManager();
                    DialogFragment fragment = new AddDocument();
                    fragment.show(mrg,"Add Document");
                });
                break;
            case "PPK":
                holder.binding.namaTerdakwa.setText(models.get(position).identitas);
                holder.binding.dakwaan.setText(models.get(position).dakwaan);
                holder.binding.garis.setBackground(context.getResources().getDrawable(R.color.red));
                holder.binding.t1.setText("Nomor");
                holder.binding.t2.setText("PPK");
                holder.binding.t3.setText("Tanggal");
                holder.binding.t4.setText("Jenis");
                holder.binding.t5.setText("Dakwaan");

                holder.binding.v2.setText(models.get(position).fullname);
                holder.binding.v1.setText(models.get(position).nomor);
                holder.binding.v3.setText(models.get(position).tanggal);
                holder.binding.v4.setText(models.get(position).jenis);
                holder.binding.v5.setText(models.get(position).dakwaan);
                holder.binding.vwmodel.setOnClickListener(v -> {
                    DocumentModel.i = models.get(position);
                    DocumentModel.ShowDetailDocument = 5;
                    FragmentActivity frg = (FragmentActivity)(context);
                    FragmentManager mrg = frg.getSupportFragmentManager();
                    DialogFragment fragment = new AddDocument();
                    fragment.show(mrg,"Add Document");
                });
                break;
        }
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
