package com.prio.kejaksaan.layer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.activity.Login;
import com.prio.kejaksaan.adapter.AdapterAtk;
import com.prio.kejaksaan.databinding.FragPersedianBinding;
import com.prio.kejaksaan.databinding.ModelAtkReqBinding;
import com.prio.kejaksaan.databinding.ModelPerkaraBinding;
import com.prio.kejaksaan.model.AtkItemModel;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.ModelLaporanATK;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.PagerAdapter;
import com.prio.kejaksaan.views.atk.AddATK;
import com.prio.kejaksaan.views.atk.AtkPermintaan;
import com.prio.kejaksaan.views.atk.AtkVerifikasi;
import com.prio.kejaksaan.views.atk.Gudang;
import com.prio.kejaksaan.views.document.SemuaPerkara;
import com.prio.kejaksaan.views.document.SemuaSurat;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Persediaan extends Fragment {

    FragPersedianBinding binding;
    PagerAdapter adapter;
    AdapterSelainLogistik adapterSelainLogistik;
    AdapterLaporanAtk adapterLaporanAtk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragPersedianBinding.inflate(inflater,container,false);

        switch (UserModel.i.type){
            case "PP":
                showTabsPP();
                binding.btnAddTop.setOnClickListener(v -> {
                    AtkModel.StatusAddATK = 1;
                    DialogFragment dialogFragment = new AddATK();
                    dialogFragment.show(requireActivity().getSupportFragmentManager(),"Add ATK");
                });
                break;
            case "Pengelola Persediaan":
                showTabsLog();
                binding.btnAddTop.setOnClickListener(v -> {
                    AtkModel.StatusAddATK = 0;
                    DialogFragment dialogFragment = new AddATK();
                    dialogFragment.show(requireActivity().getSupportFragmentManager(),"Add ATK");
                });
                break;
            case "PPK":
                binding.shimer.setVisibility(View.VISIBLE);
                binding.shimer.startShimmer();
                GetATkPPK();
                binding.layoutTabs.setVisibility(View.GONE);
                binding.layoutList.setVisibility(View.VISIBLE);
                binding.btnAddTop.setVisibility(View.GONE);
                break;
            case "KPA":
                binding.shimer.setVisibility(View.VISIBLE);
                binding.shimer.startShimmer();
                GetLaporanATKPPK();
                binding.layoutTabs.setVisibility(View.GONE);
                binding.layoutList.setVisibility(View.VISIBLE);
                binding.btnAddTop.setVisibility(View.GONE);
                break;
        }
        return binding.getRoot();
    }

    public void showTabsLog(){
        binding.tabMenu.setupWithViewPager(binding.viewpager);
        adapter = new PagerAdapter(getChildFragmentManager());

        adapter.AddFragment(new AtkPermintaan(), "All Request");
        adapter.AddFragment(new AtkVerifikasi(), "Verified");
        adapter.AddFragment(new Gudang(), "All ATK");
        binding.viewpager.setAdapter(adapter);

        for(int i=0; i < binding.tabMenu.getTabCount(); i++) {
            View tab = ((ViewGroup) binding.tabMenu.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(30, 0, 10, 0);
            tab.requestLayout();
        }
    }

    public void showTabsPP(){
        binding.tabMenu.setupWithViewPager(binding.viewpager);
        adapter = new PagerAdapter(getChildFragmentManager());

        adapter.AddFragment(new AtkVerifikasi(), "Verified");
        adapter.AddFragment(new AtkPermintaan(), "All Request");
        binding.viewpager.setAdapter(adapter);

        for(int i=0; i < binding.tabMenu.getTabCount(); i++) {
            View tab = ((ViewGroup) binding.tabMenu.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(30, 0, 10, 0);
            tab.requestLayout();
        }
    }

//    public void GetAllAtk(){
//        Call<AtkItemModel> call = BaseModel.i.getService().AllAtk();
//        call.enqueue(new Callback<AtkItemModel>() {
//            @Override
//            public void onResponse(@NotNull Call<AtkItemModel> call, @NotNull Response<AtkItemModel> response) {
//                AtkItemModel data = response.body();
//                if (Calling.TreatResponse(requireContext(), "list_atk", data)) {
//
////                    for(AtkItemModel.Item a : data.data){
//////                        a.toString();
////                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<AtkItemModel> call, @NotNull Throwable t) {
//                Log.e(TAG, "onFailure: ", t);
//            }
//        });
//    }

    public void GetATkPPK(){
        Call<AtkModel> call = BaseModel.i.getService().ATkreqPPK(BaseModel.i.token);
        call.enqueue(new Callback<AtkModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
                AtkModel atkModel = response.body();
                if(Calling.TreatResponse(getContext(),"req atk pp", atkModel)){
                    assert atkModel != null;
                    if (atkModel.data.size() == 0){
                        binding.kosongList.setVisibility(View.VISIBLE);
                    }
                    adapterSelainLogistik = new AdapterSelainLogistik(requireContext(), atkModel.data);
                    binding.listPersediaan.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
                    binding.listPersediaan.setAdapter(adapterSelainLogistik);
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

    public void GetLaporanATKPPK(){
        Call<List<ModelLaporanATK>> call = BaseModel.i.getService().LaporanAtkPPK();
        call.enqueue(new Callback<List<ModelLaporanATK>>() {
            @Override
            public void onResponse(@NotNull Call<List<ModelLaporanATK>> call, @NotNull Response<List<ModelLaporanATK>> response) {
                List<ModelLaporanATK> data = response.body();
                adapterLaporanAtk = new AdapterLaporanAtk(requireContext(), data);
                binding.listPersediaan.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true));
                binding.listPersediaan.setAdapter(adapterLaporanAtk);
                binding.shimer.stopShimmer();
                binding.shimer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<List<ModelLaporanATK>> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void GetATkPPKVerify(int ids){
        Call<PerkaraModel> call = BaseModel.i.getService().ReqATKPPK(BaseModel.i.token, ids);
        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel atkModel = response.body();
                if(Calling.TreatResponse(requireContext(),"req atk pp", atkModel)){
                    assert atkModel != null;
                    MDToast.makeText(requireContext(), "Request Berhasil di verifikasi !", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    getFragmentManager().beginTransaction().detach(Layer_Persediaan.this).attach(Layer_Persediaan.this).commit();
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public static class AdapterLaporanAtk extends RecyclerView.Adapter<AdapterLaporanAtk.vHolder>{

        Context context;
        List<ModelLaporanATK> models;

        public AdapterLaporanAtk(Context context, List<ModelLaporanATK> models) {
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

            holder.binding.dakwaan.setText(models.get(position).keterangan);
            holder.binding.namaTerdakwa.setText(models.get(position).name);
            holder.binding.t1.setText("Masuk");
            holder.binding.t2.setText("Keluar");
            holder.binding.v1.setText(String.valueOf(models.get(position).sisa_masuk));
            holder.binding.v2.setText(String.valueOf(models.get(position).keluar));
            holder.binding.l3.setVisibility(View.GONE);
            holder.binding.l4.setVisibility(View.GONE);
            holder.binding.l5.setVisibility(View.GONE);
            holder.binding.l6.setVisibility(View.GONE);

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

    public class AdapterSelainLogistik extends RecyclerView.Adapter<AdapterSelainLogistik.vHolder>{

        Context context;
        List<AtkModel> models;

        public AdapterSelainLogistik(Context context, List<AtkModel> models) {
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
            switch (UserModel.i.type){
                case "PPK":
                    if (models.get(position).ppk_id != 0){
                        holder.binding.namaTerdakwa.setVisibility(View.GONE);
                    } else {
                        holder.binding.namaTerdakwa.setText("Verifikasi");
                        holder.binding.namaTerdakwa.setGravity(Gravity.END);
                        holder.binding.namaTerdakwa.setTextColor(context.getColor(R.color.colorPrimaryDark));
                        holder.binding.namaTerdakwa.setOnClickListener(v -> GetATkPPKVerify(models.get(position).id));
                    }
                    break;
            }

            holder.binding.dakwaan.setVisibility(View.GONE);
            holder.binding.t1.setText("PPK");
            holder.binding.t2.setText("Logistik");
            if (models.get(position).ppk_id != 0){
                holder.binding.v1.setText("Verified");
                holder.binding.v1.setTextColor(context.getColor(R.color.green));
            } else {
                holder.binding.v1.setText("Unverified");
                holder.binding.v1.setTextColor(context.getColor(R.color.red));
            }

            if (models.get(position).log_id != 0){
                holder.binding.v2.setText("Verified");
                holder.binding.v2.setTextColor(context.getColor(R.color.green));
            } else {
                holder.binding.v2.setText("Unverified");
                holder.binding.v2.setTextColor(context.getColor(R.color.red));
            }

            holder.binding.l3.setVisibility(View.GONE);
            holder.binding.l4.setVisibility(View.GONE);
            holder.binding.l5.setVisibility(View.GONE);
            holder.binding.l6.setVisibility(View.GONE);

            AdapterAnak adapterAnak = new AdapterAnak(context, models.get(position).barang);
            holder.binding.listOnmodel.setVisibility(View.VISIBLE);
            holder.binding.listOnmodel.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true));
            holder.binding.listOnmodel.setAdapter(adapterAnak);
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        public class vHolder extends RecyclerView.ViewHolder{
            ModelPerkaraBinding binding;
            public vHolder(ModelPerkaraBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }
        }
    }

    public static class AdapterAnak extends RecyclerView.Adapter<AdapterAnak.vHolder>{

        Context context;
        List<AtkModel> models;

        public AdapterAnak(Context context, List<AtkModel> models) {
            this.context = context;
            this.models = models;
        }

        @NonNull
        @Override
        public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new vHolder(ModelAtkReqBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull vHolder holder, int position) {
            holder.binding.r1.setVisibility(View.GONE);
            holder.binding.r2.setVisibility(View.VISIBLE);
            holder.binding.nameDetail1.setText("Nama : " + models.get(position).name);
            holder.binding.jumlahDetail1.setText("Jumlah : " + String.valueOf(models.get(position).jumlah) + " Items");
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        public static class vHolder extends RecyclerView.ViewHolder{
            ModelAtkReqBinding binding;
            public vHolder(ModelAtkReqBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }
        }
    }

}
