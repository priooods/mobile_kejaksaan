package com.prio.kejaksaan.layer;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.prio.kejaksaan.databinding.FragDocumentBinding;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.DocumentModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.PagerAdapter;
import com.prio.kejaksaan.views.document.SemuaPerkara;
import com.prio.kejaksaan.views.document.SemuaSurat;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class Layer_Document extends Fragment {

    FragDocumentBinding binding;
    PagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragDocumentBinding.inflate(inflater, container, false);


        binding.shimer.startShimmer();


        return binding.getRoot();
    }

    public void showTabs(){
        binding.tabMenu.setupWithViewPager(binding.viewpager);
        adapter = new PagerAdapter(getChildFragmentManager());

        adapter.AddFragment(new SemuaPerkara(), "All Perkara");
        adapter.AddFragment(new SemuaSurat(), "History Surat");
        binding.viewpager.setAdapter(adapter);

        for(int i=0; i < binding.tabMenu.getTabCount(); i++) {
            View tab = ((ViewGroup) binding.tabMenu.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(30, 0, 10, 0);
            tab.requestLayout();
        }
    }

    public void GetPerkaraSudahDiProsess(){
        Call<PerkaraModel> call = BaseModel.i.getService().PerkaraSudahDiProsess();
        call.enqueue(new Callback<PerkaraModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel baseModel = response.body();
                if (Calling.TreatResponse(requireContext(),"Perkara Sudah Di Prosess", baseModel)){
                    assert baseModel != null;
                    PerkaraModel.perkaradiproses = baseModel.data.perkara;
                    showTabs();
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void GetAllSuratPanmud(){
        Call<DocumentModel> call = BaseModel.i.getService().AllPanmudSurat(BaseModel.i.token);
        call.enqueue(new Callback<DocumentModel>() {
            @Override
            public void onResponse(@NotNull Call<DocumentModel> call, @NotNull Response<DocumentModel> response) {
                DocumentModel baseModel = response.body();
                if (Calling.TreatResponse(requireContext(),"Perkara Sudah Di Prosess", baseModel)){
                    assert baseModel != null;
                    DocumentModel.semuasuratPanmud = baseModel.data;
                }
            }

            @Override
            public void onFailure(@NotNull Call<DocumentModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void GetTugasJurusita(){
        Call<PerkaraModel> call = BaseModel.i.getService().TugasJurusita(BaseModel.i.token);
        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel baseModel = response.body();
                if (Calling.TreatResponse(requireContext(),"Notify Jurusita", baseModel)){
                    assert baseModel != null;
                    PerkaraModel.notifyJurusita = baseModel.data.perkara;
                    showTabs();
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void GetAllTugasJurusita(){
        Call<DocumentModel> call = BaseModel.i.getService().AllJurusitaTugas(BaseModel.i.token);
        call.enqueue(new Callback<DocumentModel>() {
            @Override
            public void onResponse(@NotNull Call<DocumentModel> call, @NotNull Response<DocumentModel> response) {
                DocumentModel baseModel = response.body();
                if (Calling.TreatResponse(requireContext(),"Semua Tugas Jurusita", baseModel)){
                    assert baseModel != null;
                    DocumentModel.semuaTugasJurusita = baseModel.data;
                }
            }

            @Override
            public void onFailure(@NotNull Call<DocumentModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void GetTugasPPK(){
        Call<PerkaraModel> call = BaseModel.i.getService().TugasPPK(BaseModel.i.token);
        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel baseModel = response.body();
                if (Calling.TreatResponse(requireContext(),"Notify PPK", baseModel)){
                    assert baseModel != null;
                    PerkaraModel.notifyPPK = baseModel.data.perkara;
                    Log.i(TAG, "onResponse: " + baseModel.data.perkara);
                    showTabs();
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void GetAllTugasPPK(){
        Call<DocumentModel> call = BaseModel.i.getService().AllPPkTugas(BaseModel.i.token);
        call.enqueue(new Callback<DocumentModel>() {
            @Override
            public void onResponse(@NotNull Call<DocumentModel> call, @NotNull Response<DocumentModel> response) {
                DocumentModel baseModel = response.body();
                if (Calling.TreatResponse(requireContext(),"Semua Tugas Jurusita", baseModel)){
                    assert baseModel != null;
                    DocumentModel.semuaTugasPPK = baseModel.data;
                }
            }

            @Override
            public void onFailure(@NotNull Call<DocumentModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        switch (UserModel.i.type){
            case "Panmud":
                GetPerkaraSudahDiProsess();
                GetAllSuratPanmud();
                break;
            case "Jurusita":
                GetTugasJurusita();
                GetAllTugasJurusita();
                break;
            case "PPK":
                GetTugasPPK();
                GetAllTugasPPK();
                break;
        }
    }
}
