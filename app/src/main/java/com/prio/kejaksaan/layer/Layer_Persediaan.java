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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.prio.kejaksaan.adapter.AdapterRequestATK;
import com.prio.kejaksaan.databinding.FragPersedianBinding;
import com.prio.kejaksaan.databinding.ModelPerkaraBinding;
import com.prio.kejaksaan.model.AtkRequest;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.ModelLaporanATK;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.tools.PagerAdapter;
import com.prio.kejaksaan.views.atk.AddATK;
import com.prio.kejaksaan.views.atk.AtkPermintaan;
import com.prio.kejaksaan.views.atk.AtkVerifikasi;
import com.prio.kejaksaan.views.atk.Gudang;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Persediaan extends Fragment implements goFilter{

    FragPersedianBinding binding;
    PagerAdapter adapter;
    AdapterRequestATK adapterSelainLogistik;
    AdapterLaporanAtk adapterLaporanAtk;
    goFilter myPersediaan;
    goFilter[] myPersediaans;
    boolean layerLaporan;
    int ID = new Random().nextInt(1000);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragPersedianBinding.inflate(inflater,container,false);

        layerLaporan = false;
        switch (UserModel.i.type){
            case "PP":
                showTabs(false,"Semua","Belum Terima");
                binding.btnAddTop.setOnClickListener(v -> {
                    DialogFragment dialogFragment = new AddATK(1);
                    dialogFragment.show(requireActivity().getSupportFragmentManager(),"Add ATK");
                });
                break;
            case "Pengelola Persediaan":
                showTabs(true,"Semua","Belum Serah");
                binding.btnAddTop.setOnClickListener(v -> {
                    DialogFragment dialogFragment = new AddATK(0);
                    dialogFragment.show(requireActivity().getSupportFragmentManager(),"Add ATK");
                });
                break;
            case "PPK":
                showTabs(false,"Semua","Belum Verifikasi");
                binding.btnAddTop.setVisibility(View.GONE);
                break;
            case "KPA":
            case "SuperUser":
                binding.shimer.setVisibility(View.VISIBLE);
                binding.shimer.startShimmer();
                myPersediaan = this;
                layerLaporan = true;
                GetLaporanATK();
                binding.layoutTabs.setVisibility(View.GONE);
                binding.layoutList.setVisibility(View.VISIBLE);
                binding.btnAddTop.setVisibility(View.GONE);
                break;
        }

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
                Log.e("ATKFILTER",getID()+" On Text Changed");
                myPersediaan.Filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        return binding.getRoot();
    }

    public void showTabs(boolean Gudang, String verified, String unverified) {
        binding.tabMenu.setupWithViewPager(binding.viewpager);
        if (adapter != null) {
            binding.viewpager.setAdapter(adapter);

            for (int i = 0; i < binding.tabMenu.getTabCount(); i++) {
                View tab = ((ViewGroup) binding.tabMenu.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                p.setMargins(30, 0, 10, 0);
                tab.requestLayout();
            }
            return;
        }
        adapter = new PagerAdapter(getChildFragmentManager());
        myPersediaans = new goFilter[3];
        adapter.AddFragment((Fragment) (myPersediaan = myPersediaans[0] = new AtkPermintaan()), verified);
        Log.e("ATKFILTER",ID+" myPersediaan refresh to "+myPersediaan.getID()+"!");
        adapter.AddFragment((Fragment) (myPersediaans[1] = new AtkVerifikasi()), unverified); //semua
        if (Gudang)
            adapter.AddFragment((Fragment) (myPersediaans[2] = new Gudang()), "All ATK");
        binding.viewpager.setAdapter(adapter);


        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                myPersediaan = myPersediaans[position];
            }
        });

        for (int i = 0; i < binding.tabMenu.getTabCount(); i++) {
            View tab = ((ViewGroup) binding.tabMenu.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(30, 0, 10, 0);
            tab.requestLayout();
        }
    }

    public void GetLaporanATK(){
        Call<List<ModelLaporanATK>> call = BaseModel.i.getService().LaporanAtkPPK();
        call.enqueue(new Callback<List<ModelLaporanATK>>() {
            @Override
            public void onResponse(@NotNull Call<List<ModelLaporanATK>> call, @NotNull Response<List<ModelLaporanATK>> response) {
                List<ModelLaporanATK> data = response.body();
                if (getContext() == null)
                    return;
                adapterLaporanAtk = new AdapterLaporanAtk(requireContext(), data);
                binding.listPersediaan.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
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

    @Override
    public void Filter(CharSequence filters) {
        if (layerLaporan)
            adapterLaporanAtk.getFilter().filter(filters);
        else{
            myPersediaan.Filter(filters);
        }
    }
    @Override
    public int getID() {
        return ID;
    }

    public static class AdapterLaporanAtk extends RecyclerView.Adapter<AdapterLaporanAtk.vHolder>{

        Context context;
        List<ModelLaporanATK> models,unfilter;

        public AdapterLaporanAtk(Context context, List<ModelLaporanATK> models) {
            this.context = context;
            this.models = unfilter = models;
        }

        @NonNull
        @Override
        public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new vHolder(ModelPerkaraBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
                    List<ModelLaporanATK> modelss = new ArrayList<>();
                    for (ModelLaporanATK model : unfilter) {
                        if (model.name.toLowerCase().contains(key) ||
                                model.keterangan.toLowerCase().contains(key) ||
                                String.valueOf(model.keluar).toLowerCase().contains(key) ||
                                String.valueOf(model.masuk).contains(key) ||
                                String.valueOf(model.sisa).contains(key)) {
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
                    models = (List<ModelLaporanATK>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
        @Override
        public void onBindViewHolder(@NonNull vHolder holder, int position) {

            holder.binding.dakwaan.setText(models.get(position).keterangan);
            holder.binding.namaTerdakwa.setText(models.get(position).name);
            holder.binding.t1.setText("Masuk");
            holder.binding.t2.setText("Keluar");
            holder.binding.t3.setText("Sisa");
            holder.binding.v1.setText(String.valueOf(models.get(position).masuk));
            holder.binding.v2.setText(String.valueOf(models.get(position).keluar));
            holder.binding.v3.setText(String.valueOf(models.get(position).sisa));
            holder.binding.l4.setVisibility(View.GONE);
            holder.binding.l5.setVisibility(View.GONE);
            holder.binding.l6.setVisibility(View.GONE);

        }

        @Override
        public int getItemCount() {
            if (models == null){
                return 0;
            }
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
