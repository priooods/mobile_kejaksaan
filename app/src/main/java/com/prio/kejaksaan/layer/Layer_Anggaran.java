package com.prio.kejaksaan.layer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.adapter.AdapterSurat;
import com.prio.kejaksaan.databinding.FragAnggaranBinding;
import com.prio.kejaksaan.databinding.ModelPerkaraBinding;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.views.atk.AtkVerifikasi;
import com.prio.kejaksaan.views.document.AddDocument;
import com.prio.kejaksaan.views.perkara.DetailPerkara;
import com.prio.kejaksaan.views.profile.EditProfile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Anggaran extends Fragment {

    FragAnggaranBinding binding;
    AdapterAnggaran adapterAnggaran;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragAnggaranBinding.inflate(inflater,container,false);
        binding.shimer.startShimmer();
        switch (UserModel.i.type){
            case "PPK":
            case "Ketua":
                ListforPPK();
                binding.btnAddNew.setOnClickListener(v -> {
                    DocumentModel.ShowDetailDocument = 6;
                    DialogFragment dialogFragment = new AddDocument();
                    dialogFragment.show(requireActivity().getSupportFragmentManager(),"Add Document");
                });
                break;
            case "Bendahara":
                binding.btnAddNew.setVisibility(View.GONE);
                ListforBendahara();
                break;
        }


        return binding.getRoot();
    }

    public void ListforPPK(){
        Call<AtkModel> call = BaseModel.i.getService().PembyaranALLPPK();
        call.enqueue(new Callback<AtkModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
                AtkModel data = response.body();
                if (Calling.TreatResponse(requireContext(),"Calling Pembayaran", data)){
                    assert data != null;
                    adapterAnggaran = new AdapterAnggaran(requireContext(), data.data);
                    binding.listanggaran.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
                    binding.listanggaran.setHasFixedSize(true);
                    binding.listanggaran.setAdapter(adapterAnggaran);
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void ListforBendahara(){
        Call<AtkModel> call = BaseModel.i.getService().PembayaranAllBendahara(BaseModel.i.token);
        call.enqueue(new Callback<AtkModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
                AtkModel data = response.body();
                if (Calling.TreatResponse(requireContext(),"Calling Pembayaran", data)){
                    assert data != null;
                    adapterAnggaran = new AdapterAnggaran(requireContext(), data.data);
                    binding.listanggaran.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
                    binding.listanggaran.setHasFixedSize(true);
                    binding.listanggaran.setAdapter(adapterAnggaran);
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public static class AdapterAnggaran extends RecyclerView.Adapter<AdapterAnggaran.vHolder>{

        Context context;
        List<AtkModel> models;

        public AdapterAnggaran(Context context, List<AtkModel> models) {
            this.context = context;
            this.models = models;
        }

        @NonNull
        @Override
        public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new vHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull vHolder holder, int position) {
            holder.binding.dakwaan.setVisibility(View.GONE);
            holder.binding.namaTerdakwa.setText(models.get(position).fullname);
            holder.binding.t1.setText("Surat");
            holder.binding.t2.setText("Kwitansi");
            holder.binding.v1.setText(models.get(position).surat);
            holder.binding.v2.setTextColor(context.getColor(R.color.red));
            holder.binding.v2.setText("Unverified");
            if (models.get(position).kuitansi != null){
                holder.binding.v2.setText(models.get(position).kuitansi);
                holder.binding.v2.setTextColor(context.getColor(R.color.colorPrimary));
            }

            holder.binding.l3.setVisibility(View.GONE);
            holder.binding.l4.setVisibility(View.GONE);
            holder.binding.l5.setVisibility(View.GONE);
            holder.binding.l6.setVisibility(View.GONE);

            if (UserModel.i.type.equals("Bendahara")){
                holder.binding.card.setOnClickListener(v -> {
                    DocumentModel.ShowDetailDocument = 7;
                    AtkModel.i = models.get(position);
                    FragmentActivity frg = (FragmentActivity)(context);
                    FragmentManager mrg = frg.getSupportFragmentManager();
                    DialogFragment fragment = new AddDocument();
                    fragment.show(mrg,"Add Document");
                });
            } else {
                Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse("https://digitalsystemindo.com/jaksa/public/files/"+models.get(position).kuitansi));
                context.startActivity(web);
            }
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