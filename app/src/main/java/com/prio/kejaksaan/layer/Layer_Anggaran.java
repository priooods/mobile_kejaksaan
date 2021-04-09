package com.prio.kejaksaan.layer;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.FragAnggaranBinding;
import com.prio.kejaksaan.databinding.ModelPerkaraBinding;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.PembayaranModel;
import com.prio.kejaksaan.model.SuratModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.Laravel;
import com.prio.kejaksaan.views.document.AddDocument;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Anggaran extends Fragment {

    FragAnggaranBinding binding;
    AdapterAnggaran adapterAnggaran;
    public List<PembayaranModel.Item> model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragAnggaranBinding.inflate(inflater,container,false);
        binding.shimer.startShimmer();
        ListPembayaran();
//        switch (UserModel.i.type){
//            case "PPK":
//            case "Ketua":
//                ListPembayaran();
////                binding.btnAddNew.setOnClickListener(v -> {
//////                    DocumentModel.ShowDetailDocument = 6;
////                    DialogFragment dialogFragment = new AddDocument(6);
////                    dialogFragment.show(requireActivity().getSupportFragmentManager(),"Add Document");
////                });
//                break;
//            case "Bendahara":
////                binding.btnAddNew.setVisibility(View.GONE);
//                ListPembayaran();
//                break;
//        }
        binding.btnSearch.setOnClickListener(v -> {
            binding.search1.setVisibility(View.GONE);
            binding.search2.setVisibility(View.VISIBLE);
        });

        binding.cross.setOnClickListener(v -> {
            binding.search2.setVisibility(View.GONE);
            binding.search1.setVisibility(View.VISIBLE);
//            (PerkaraModel.listperkara);
        });
        binding.findinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                adapterAnggaran.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        return binding.getRoot();
    }


    public void ListPembayaran(){
        Call<PembayaranModel> call = BaseModel.i.getService().PembyaranALLPPK();
        call.enqueue(new Callback<PembayaranModel>() {
            @Override
            public void onResponse(@NotNull Call<PembayaranModel> call, @NotNull Response<PembayaranModel> response) {
                PembayaranModel data = response.body();
                if (Calling.TreatResponse(getContext(),"Calling Pembayaran", data)){
                    assert data != null;
                    model = data.data;
                    adapterAnggaran = new AdapterAnggaran(requireContext(), data.data);
                    binding.listanggaran.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                    binding.listanggaran.setHasFixedSize(true);
                    binding.listanggaran.setAdapter(adapterAnggaran);
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PembayaranModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

//    public void ListforBendahara(){
//        Call<AtkModel> call = BaseModel.i.getService().PembayaranAllBendahara(BaseModel.i.token);
//        call.enqueue(new Callback<AtkModel>() {
//            @Override
//            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
//                AtkModel data = response.body();
//                if (Calling.TreatResponse(requireContext(),"Calling Pembayaran", data)){
//                    assert data != null;
//                    adapterAnggaran = new AdapterAnggaran(requireContext(), data.data);
//                    binding.listanggaran.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
//                    binding.listanggaran.setHasFixedSize(true);
//                    binding.listanggaran.setAdapter(adapterAnggaran);
//                    binding.shimer.stopShimmer();
//                    binding.shimer.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<AtkModel> call, @NotNull Throwable t) {
//                Log.e(TAG, "onFailure: ", t);
//            }
//        });
//    }

    public static class AdapterAnggaran extends RecyclerView.Adapter<AdapterAnggaran.vHolder>{

        Context context;
        List<PembayaranModel.Item> models, unfilter;

        public AdapterAnggaran(Context context, List<PembayaranModel.Item> models) {
            this.context = context;
            this.models = this.unfilter = models;
        }

        @NonNull
        @Override
        public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new vHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull vHolder holder, int position) {
//            holder.binding.dakwaan.setVisibility(View.GONE);
            holder.binding.namaTerdakwa.setText(models.get(position).surat_tugas.tipe);
            holder.binding.dakwaan.setText(models.get(position).surat_tugas.perkara.identitas);
            holder.binding.t1.setText("Dibuat");
            holder.binding.t2.setText("Dibayar");
            holder.binding.v1.setText(Laravel.getShortDateTime(models.get(position).created));
            if (models.get(position).kuitansi ==null){
                holder.binding.v2.setTextColor(context.getColor(R.color.red));
                holder.binding.v2.setText("Belum");
            }else{
                holder.binding.v2.setTextColor(context.getColor(R.color.green));
                holder.binding.v2.setText("Sudah");
            }
            holder.binding.t3.setText("Terlaksana");
            holder.binding.v3.setText(Laravel.getShortDateTime(models.get(position).surat_tugas.daftar_time));
            holder.binding.t5.setText("Pelaksana");
            holder.binding.v5.setText(models.get(position).surat_tugas.perkara.fullname_jurusita);


//            holder.binding.l3.setVisibility(View.GONE);
            holder.binding.l4.setVisibility(View.GONE);
//            holder.binding.l5.setVisibility(View.GONE);
            holder.binding.l6.setVisibility(View.GONE);

                holder.binding.card.setOnClickListener(v -> {
//                    DocumentModel.ShowDetailDocument = 7;
//                    AtkModel.i = models.get(position);
                    FragmentActivity frg = (FragmentActivity)(context);
                    FragmentManager mrg = frg.getSupportFragmentManager();
                    DialogFragment fragment;
                    if (UserModel.i.type.equals("Bendahara")){
                        fragment = new AddDocument(7,models.get(position));
                    }else{
                        fragment = new AddDocument(6,models.get(position));
                    }
                    fragment.show(mrg,"Add Document");
                });
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
                    List<PembayaranModel.Item> modelss = new ArrayList<>();
                    for (PembayaranModel.Item model : unfilter) {
                        SuratModel.Item surat = model.surat_tugas;
                        if (surat.tipe.toLowerCase().contains(key.toLowerCase()) ||
                                surat.perkara.identitas.toLowerCase().contains(key.toLowerCase()) ||
                                surat.daftar_time.toLowerCase().contains(key.toLowerCase()) ||
                                surat.perkara.fullname_jurusita.toLowerCase().contains(key.toLowerCase()) ||
                                model.created.toLowerCase().contains(key.toLowerCase())) {
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
                    models = (List<PembayaranModel.Item>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public int getItemCount() {
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

}
