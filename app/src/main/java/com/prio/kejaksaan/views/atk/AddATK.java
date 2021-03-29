package com.prio.kejaksaan.views.atk;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.DialogAddAtkBinding;
import com.prio.kejaksaan.databinding.ModelAtkReqBinding;
import com.prio.kejaksaan.layer.Layer_Persediaan;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.service.Calling;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class AddATK extends DialogFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullscreen);
    }


    ArrayList<AtkModel> atkModels;
    DialogAddAtkBinding binding;
    String[] atkname;
    int idatk;
    String namesATKselect;
    AdapterListPermintaan adapters;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddAtkBinding.inflate(inflater,container,false);

        atkModels = new ArrayList<>();
        switch (AtkModel.StatusAddATK){
            case 0:
                binding.btnCreateUsers.setOnClickListener(v->TambahATK());
                break;
            case 1:
                binding.l1.setVisibility(View.GONE);
                binding.btnCreateUsers.setVisibility(View.GONE);
                binding.l2.setVisibility(View.VISIBLE);
                binding.btnCreateUsers.setOnClickListener(v->KirimReqATK());
                break;
        }

        binding.tambahpermintaan.setOnClickListener(v -> {
            binding.formMinta.setVisibility(View.VISIBLE);
            binding.btnCreateUsers.setVisibility(View.GONE);
        });
        binding.btnCancelMinta.setOnClickListener(v -> {
            binding.formMinta.setVisibility(View.GONE);
            binding.nameatk.setText(null);
            binding.jumlahminta.setText(null);
        });


        atkname = new String[AtkModel.atklist.size()];
        int i;
        for (i=0; i<AtkModel.atklist.size();i++){
            atkname[i] = AtkModel.atklist.get(i).name;
        }

        binding.nameatk.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapternama = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, atkname);
        binding.nameatk.setAdapter(adapternama);
        binding.nameatk.setOnItemClickListener((parent, view, position, id) -> {
            idatk = AtkModel.atklist.get(position).id;
        });

        binding.btnAddMinta.setOnClickListener(v -> {
            atkModels.add(new AtkModel(idatk, Integer.parseInt(Objects.requireNonNull(binding.jumlahminta.getText()).toString()),
                    binding.nameatk.getText().toString()));
            setListPermintaan(atkModels);
            binding.btnCreateUsers.setVisibility(View.VISIBLE);
            binding.formMinta.setVisibility(View.GONE);
            binding.nameatk.setText(null);
            binding.jumlahminta.setText(null);
        });

        return binding.getRoot();
    }

    public void setListPermintaan(ArrayList<AtkModel> mod){
        if (atkModels != null && atkModels.size() != 0){
            adapters = new AdapterListPermintaan(requireContext(), mod);
            binding.listAtk.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
            binding.listAtk.setAdapter(adapters);
            adapters.notifyDataSetChanged();
        }
    }

    public void TambahATK(){
        Call<AtkModel> call = BaseModel.i.getService().AddATK(Objects.requireNonNull(binding.name.getText()).toString(),
                Objects.requireNonNull(binding.keterangan.getText()).toString(),Integer.parseInt(Objects.requireNonNull(binding.jumlah.getText()).toString()));

        call.enqueue(new Callback<AtkModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
                AtkModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Add ATK", data)){
                    assert data != null;
                    MDToast.makeText(requireContext(), "ATK " + data.name  + " Baru berhasil di tambahkan", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Persediaan persediaan = (Layer_Persediaan) getFragmentManager().findFragmentByTag("persediaan");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert persediaan != null;
                    transaction.detach(persediaan);
                    transaction.attach(persediaan);
                    transaction.commit();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t );
            }
        });
    }

    public void KirimReqATK(){
        Log.i(TAG, "KirimReqATK: " + Arrays.toString(atkModels.toArray()));
    }

    public class AdapterListPermintaan extends RecyclerView.Adapter<AdapterListPermintaan.vHolder>{

        Context context;
        List<AtkModel> atkModelList;

        public AdapterListPermintaan(Context context, List<AtkModel> atkModelList) {
            this.context = context;
            this.atkModelList = atkModelList;
        }

        @NonNull
        @Override
        public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new vHolder(ModelAtkReqBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull vHolder holder, int position) {
            holder.binding.name.setText(atkModelList.get(position).name);
            holder.binding.jumlah.setText(String.valueOf(atkModelList.get(position).jumlah) + " Items");
            holder.binding.delete.setOnClickListener(v -> {
                atkModelList.remove(position);
                notifyItemRemoved(position);
                notifyItemChanged(position,atkModelList.size());
                notifyDataSetChanged();
                if (atkModelList.size() == 0){
                    binding.btnCreateUsers.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return atkModelList.size();
        }

        public class vHolder extends RecyclerView.ViewHolder{
            ModelAtkReqBinding binding;
            public vHolder(ModelAtkReqBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }
        }
    }
}
