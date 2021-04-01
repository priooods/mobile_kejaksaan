package com.prio.kejaksaan.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.SuratModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.views.document.AddDocument;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdapterSurat extends RecyclerView.Adapter<AdapterSurat.vHolder> {

    Context context;
    List<SuratModel.Item> models;

    public AdapterSurat(Context context, List<SuratModel.Item> models) {
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

        switch (UserModel.i.type){
            case "Jurusita":
                holder.binding.namaTerdakwa.setText(models.get(position).tipe);
                holder.binding.dakwaan.setText(models.get(position).surat_tugas);
                holder.binding.l1.setVisibility(View.GONE);
                holder.binding.l2.setVisibility(View.GONE);
                holder.binding.l3.setVisibility(View.GONE);
                holder.binding.l4.setVisibility(View.GONE);
                holder.binding.l5.setVisibility(View.GONE);
                holder.binding.l6.setVisibility(View.GONE);
                break;
            case "Panmud":
                SimpleDateFormat fr = new SimpleDateFormat("YYYY/MM/dd", Locale.ENGLISH);
                SimpleDateFormat tex = new SimpleDateFormat("dd MMM YYYY", Locale.ENGLISH);
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(Objects.requireNonNull(fr.parse(models.get(position).created)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.binding.namaTerdakwa.setText(models.get(position).tipe);
                holder.binding.dakwaan.setText(models.get(position).surat_tugas);
                holder.binding.garis.setBackground(context.getResources().getDrawable(R.color.colorPrimaryDark));
                holder.binding.t1.setText("Hari");
                holder.binding.t2.setText("Tanggal");
                holder.binding.t3.setText("Agenda");
                holder.binding.t4.setText("Nomor");
                holder.binding.t5.setText("Jenis");
                holder.binding.t6.setText("Ditahan");
                holder.binding.v1.setText(": "+models.get(position).perkara.tanggal);
                holder.binding.v2.setText(": "+tex.format(c.getTime()));
                holder.binding.v3.setText(": "+models.get(position).proses.agenda);
                holder.binding.v4.setText(": "+models.get(position).perkara.nomor);
                holder.binding.v5.setText(": "+models.get(position).perkara.jenis);
                holder.binding.v6.setText(": "+models.get(position).perkara.penahanan);
                break;
            case "PPK":
                SimpleDateFormat frs = new SimpleDateFormat("YYYY/MM/dd", Locale.ENGLISH);
                SimpleDateFormat texs = new SimpleDateFormat("dd MMM YYYY", Locale.ENGLISH);
                Calendar cs = Calendar.getInstance();
                try {
                    cs.setTime(Objects.requireNonNull(frs.parse(models.get(position).created)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.binding.namaTerdakwa.setText(models.get(position).tipe);
                holder.binding.dakwaan.setText(models.get(position).surat_tugas);
                holder.binding.garis.setBackground(context.getResources().getDrawable(R.color.colorPrimaryDark));
                holder.binding.t1.setText("Tanggal");
                holder.binding.t2.setText("Nomor");
                holder.binding.t3.setText("Jenis");
                holder.binding.t4.setText("Ditahan");
                holder.binding.v1.setText(": "+texs.format(cs.getTime()));
                holder.binding.v2.setText(": "+models.get(position).perkara.tanggal);
                holder.binding.v3.setText(": "+models.get(position).perkara.jenis);
                holder.binding.v4.setText(": "+models.get(position).perkara.penahanan);
                holder.binding.l5.setVisibility(View.GONE);
                holder.binding.l6.setVisibility(View.GONE);
                break;
        }

        holder.binding.vwmodel.setOnClickListener(v -> {
            switch (UserModel.i.type){
                case "Panmud":
                    DocumentModel.ShowDetailDocument = 1;
                    break;
                case "Jurusita":
                    DocumentModel.ShowDetailDocument = 3;
                    break;
                case "PPK":
                    DocumentModel.ShowDetailDocument = 4;
                    break;
            }
//            SuratModel.i = models.get(position);
            FragmentActivity frg = (FragmentActivity)(context);
            FragmentManager mrg = frg.getSupportFragmentManager();
            DialogFragment fragment = new AddDocument(2, models.get(position));
            fragment.show(mrg,"Add Document");
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
