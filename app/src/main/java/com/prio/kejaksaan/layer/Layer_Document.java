package com.prio.kejaksaan.layer;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.prio.kejaksaan.databinding.FragDocumentBinding;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.SuratModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.PagerAdapter;
import com.prio.kejaksaan.views.document.SemuaPerkara;
import com.prio.kejaksaan.views.document.SemuaSurat;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Document extends Fragment {

    FragDocumentBinding binding;
    PagerAdapter adapter;
    PerkaraListModel perkara_list;
    SuratModel surat_list;
    goFilter myDocument;
    goFilter[] myDocuments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragDocumentBinding.inflate(inflater, container, false);


        binding.shimer.startShimmer();

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
                myDocument.Filter(charSequence);
                Log.e("Anggaran","OnChange");
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        return binding.getRoot();
    }

    public void showTabs(boolean perkara){
        if ((perkara && perkara_list == null) || surat_list == null)
            return;
        if (adapter != null) {
            binding.tabMenu.setupWithViewPager(binding.viewpager);
            binding.viewpager.setAdapter(adapter);
            binding.shimer.stopShimmer();
            binding.shimer.setVisibility(View.GONE);

            for (int i = 0; i < binding.tabMenu.getTabCount(); i++) {
                View tab = ((ViewGroup) binding.tabMenu.getChildAt(0)).getChildAt(i);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
                p.setMargins(30, 0, 10, 0);
                tab.requestLayout();
            }
            return;
        }

        binding.shimer.stopShimmer();
        binding.shimer.setVisibility(View.GONE);
        binding.tabMenu.setupWithViewPager(binding.viewpager);
        adapter = new PagerAdapter(getChildFragmentManager());
        int position = 0;
        myDocuments = new goFilter[2];
        if (perkara) {
            adapter.AddFragment((Fragment)(myDocument = myDocuments[position] = new SemuaPerkara(perkara_list)), "Semua Perkara");
            position++;
        }
        adapter.AddFragment((Fragment)(myDocument = myDocuments[position] = new SemuaSurat(surat_list)), "Semua Surat");
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                myDocument = myDocuments[position];
            }
        });

        for(int i=0; i < binding.tabMenu.getTabCount(); i++) {
            View tab = ((ViewGroup) binding.tabMenu.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(30, 0, 10, 0);
            tab.requestLayout();
        }
    }

    public void PanmudPerkara(){
        Call<PerkaraListModel> call = BaseModel.i.getService().PerkaraSudahDiProsess();
        call.enqueue(new Callback<PerkaraListModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<PerkaraListModel> call, @NotNull Response<PerkaraListModel> response) {
                PerkaraListModel baseModel = response.body();
                if (Calling.TreatResponse(getContext(),"Perkara Sudah Di Prosess", baseModel)){
                    assert baseModel != null;
                    perkara_list = baseModel;
                    showTabs(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraListModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void PanmudSurat(){
        Call<SuratModel> call = BaseModel.i.getService().AllPanmudSurat(BaseModel.i.token);
        call.enqueue(new Callback<SuratModel>() {
            @Override
            public void onResponse(@NotNull Call<SuratModel> call, @NotNull Response<SuratModel> response) {
                SuratModel baseModel = response.body();
                if (getContext() == null)
                    return;
                if (Calling.TreatResponse(requireContext(),"Perkara Sudah Di Prosess", baseModel)){
                    assert baseModel != null;
                    surat_list = baseModel;
                    showTabs(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<SuratModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

//    public void GetTugasJurusita(){
//        Call<PerkaraModel> call = BaseModel.i.getService().TugasJurusita(BaseModel.i.token);
//        call.enqueue(new Callback<PerkaraModel>() {
//            @Override
//            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
//                PerkaraModel baseModel = response.body();
//                if (Calling.TreatResponse(requireContext(),"Notify Jurusita", baseModel)){
//                    assert baseModel != null;
//                    PerkaraModel.notifyJurusita = baseModel.data.perkara;
//                    showTabs(true);
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
//                Log.e(TAG, "onFailure: ", t);
//            }
//        });
//    }

    public void JurusitaSurat(){
        Call<SuratModel> call = BaseModel.i.getService().AllJurusitaTugas(BaseModel.i.token);
        call.enqueue(new Callback<SuratModel>() {
            @Override
            public void onResponse(@NotNull Call<SuratModel> call, @NotNull Response<SuratModel> response) {
                SuratModel baseModel = response.body();
                if (Calling.TreatResponse(getContext(),"Semua Tugas Jurusita", baseModel)){
                    assert baseModel != null;
                    surat_list = baseModel;
                    showTabs(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<SuratModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

//    public void GetTugasPPK(){
//        Call<SuratModel> call = BaseModel.i.getService().TugasPPK(BaseModel.i.token);
//        call.enqueue(new Callback<SuratModel>() {
//            @Override
//            public void onResponse(@NotNull Call<SuratModel> call, @NotNull Response<SuratModel> response) {
//                SuratModel baseModel = response.body();
//                if (Calling.TreatResponse(requireContext(),"Notify PPK", baseModel)){
//                    surat_list = baseModel;
//                    showTabs(true);
////                    PerkaraModel.notifyPPK = baseModel.data.perkara;
////                    Log.i(TAG, "onResponse: " + baseModel.data.perkara);
//                }
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<SuratModel> call, @NotNull Throwable t) {
//                Log.e(TAG, "onFailure: ", t);
//            }
//        });
//    }

    public void PPKSurat(){
        Call<SuratModel> call = BaseModel.i.getService().AllPPkTugas(BaseModel.i.token);
        call.enqueue(new Callback<SuratModel>() {
            @Override
            public void onResponse(@NotNull Call<SuratModel> call, @NotNull Response<SuratModel> response) {
                SuratModel baseModel = response.body();
                if (Calling.TreatResponse(getContext(),"PPK Surat", baseModel)){
                    assert baseModel != null;
                    surat_list = baseModel;
                    showTabs(false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<SuratModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        switch (UserModel.i.type){
            case "Panmud":
                PanmudPerkara();
                PanmudSurat();
                break;
            case "Jurusita":
//                GetTugasJurusita();
                JurusitaSurat();
                break;
            case "PPK":
//                PPKPerkara();
                PPKSurat();
                break;
        }
    }
}
