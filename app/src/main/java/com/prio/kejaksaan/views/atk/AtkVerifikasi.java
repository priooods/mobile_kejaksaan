package com.prio.kejaksaan.views.atk;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.prio.kejaksaan.adapter.AdapterRequestATK;
import com.prio.kejaksaan.databinding.FragAtkReqBinding;
import com.prio.kejaksaan.layer.goFilter;
import com.prio.kejaksaan.model.AtkRequest;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.MessageModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.valdesekamdem.library.mdtoast.MDToast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;


import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
public class AtkVerifikasi extends Fragment implements goFilter {

    FragAtkReqBinding binding;
    AdapterRequestATK adapterSelainLogistik;
    DialogFragment dialog;
    int ID = new Random().nextInt(1000);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragAtkReqBinding.inflate(inflater, container, false);
        binding.shimer.startShimmer();
        switch (UserModel.i.type){
            case "Pengelola Persediaan":
                binding.desc.setText("Semua riwayat permintaan ATK");
                GetATkVerifLog();
                break;
            case "PP":
                binding.desc.setText("Daftar Request ATK yang butuh diverifikasi");
                GetATkVerifPP();
                break;
            case "PPK":
                binding.desc.setText("Daftar Request ATK yang butuh diverifikasi");
                GetATkVerifPPK();
                break;

        }
        return binding.getRoot();
    }

    public void LogAdapter(List<AtkRequest.Item> list){
        adapterSelainLogistik = new AdapterRequestATK(requireContext(), list){
            @Override
            public boolean VerifierButton(AtkRequest.Item req) {
                return req.penerimaan == null;
            }

            @Override
            public void VerifierAction(int position, AtkRequest.Item item) {
                Log.e("PP","Verifikasi!");
//                SetAtkVerifPP(position, item);
                dialog = new AddATK(2, item);
                dialog.show(requireActivity().getSupportFragmentManager(),"Add ATK");
            }
        };
    }
    public void PPAdapter(List<AtkRequest.Item> list){
        adapterSelainLogistik = new AdapterRequestATK(requireContext(), list){
            @Override
            public boolean VerifierButton(AtkRequest.Item req) {
                return req.penerimaan == null;
            }

            @Override
            public void VerifierAction(int position, AtkRequest.Item item) {
                dialog = new AddATK(3, item);
                dialog.show(requireActivity().getSupportFragmentManager(),"Add ATK");
            }
        };
    }
    public void PPKAdapter(List<AtkRequest.Item> list){
        adapterSelainLogistik = new AdapterRequestATK(requireContext(), list){
            @Override
            public boolean VerifierButton(AtkRequest.Item req) {
                return req.ppk_id == null;
            }

            @Override
            public void VerifierAction(int position, AtkRequest.Item item) {
                Log.e("PPK","Verifikasi!");
                SetAtkVerifPPK(position, item);
                binding.shimer.setVisibility(View.VISIBLE);
                binding.shimer.startShimmer();
            }
        };
    }

    public void GetATkVerifLog(){
        Call<AtkRequest> call = BaseModel.i.getService().ATkverifLog(BaseModel.i.token);
        call.enqueue(new Callback<AtkRequest>() {
            @Override
            public void onResponse(@NotNull Call<AtkRequest> call, @NotNull Response<AtkRequest> response) {
                AtkRequest atkModel = response.body();
                if(Calling.TreatResponse(getContext(),"req atk Log", atkModel)){
                    assert atkModel != null;
                    LogAdapter(atkModel.data);
                    binding.listAtkReq.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                    binding.listAtkReq.setAdapter(adapterSelainLogistik);
                    adapterSelainLogistik.notifyDataSetChanged();
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkRequest> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void GetATkVerifPP(){
        Call<AtkRequest> call = BaseModel.i.getService().ATkverifPP(BaseModel.i.token);
        call.enqueue(new Callback<AtkRequest>() {
            @Override
            public void onResponse(@NotNull Call<AtkRequest> call, @NotNull Response<AtkRequest> response) {
                AtkRequest atkModel = response.body();
                if(Calling.TreatResponse(getContext(),"req atk Log", atkModel)){
                    assert atkModel != null;
                    PPAdapter(atkModel.data);
                    Log.e("PP Log Verify", "setout "+atkModel.data.size());
                    binding.listAtkReq.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                    binding.listAtkReq.setAdapter(adapterSelainLogistik);
                    adapterSelainLogistik.notifyDataSetChanged();
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkRequest> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void GetATkVerifPPK(){
        Call<AtkRequest> call = BaseModel.i.getService().ATkverifPPK(BaseModel.i.token);
        call.enqueue(new Callback<AtkRequest>() {
            @Override
            public void onResponse(@NotNull Call<AtkRequest> call, @NotNull Response<AtkRequest> response) {
                AtkRequest atkModel = response.body();
                if(Calling.TreatResponse(getContext(),"req atk Log", atkModel)){
                    assert atkModel != null;
                    PPKAdapter(atkModel.data);
                    Log.e("PP Log Verify", "setout "+atkModel.data.size());
                    binding.listAtkReq.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                    binding.listAtkReq.setAdapter(adapterSelainLogistik);
                    adapterSelainLogistik.notifyDataSetChanged();
                    binding.shimer.stopShimmer();
                    binding.shimer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<AtkRequest> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
//    public void SetAtkVerifPP(int position, AtkRequest.Item item){
//        Call<MessageModel> call = BaseModel.i.getService().AtkaccPP(BaseModel.i.token, item.id);
//        call.enqueue(new Callback<MessageModel>() {
//            @Override
//            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
//                MessageModel model = response.body();
//                if(Calling.TreatResponse(getContext(),"req atk Log", model)){
//                    assert model != null;
//                    item.penerimaan = "YES";
//                    adapterSelainLogistik.notifyItemChanged(position);
//                    MDToast.makeText(getContext(), model.data,  Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
//                }
//                binding.shimer.stopShimmer();
//                binding.shimer.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
//                Log.e(TAG, "onFailure: ", t);
//                binding.shimer.stopShimmer();
//                binding.shimer.setVisibility(View.GONE);
//            }
//        });
//    }

    public void SetAtkVerifLOG(int position, AtkRequest.Item item){
        Call<MessageModel> call = BaseModel.i.getService().VerifyATKLog(BaseModel.i.token, item.id, null);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel model = response.body();
                if(Calling.TreatResponse(requireContext(),"req atk pp", model)){
                    assert model != null;
                    item.log_id = UserModel.i.id;
                    adapterSelainLogistik.notifyItemChanged(position);
                    MDToast.makeText(getContext(), model.data,  Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void SetAtkVerifPPK(int position, AtkRequest.Item item){
        Call<MessageModel> call = BaseModel.i.getService().AtkaccPPK(BaseModel.i.token, item.id);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel model = response.body();
                if(Calling.TreatResponse(requireContext(),"req atk pp", model)){
                    assert model != null;
                    item.ppk_id = UserModel.i.id;
                    adapterSelainLogistik.notifyItemChanged(position);
                    MDToast.makeText(getContext(), model.data,  Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                }
                binding.shimer.stopShimmer();
                binding.shimer.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public void Filter(CharSequence filters) {
        adapterSelainLogistik.getFilter().filter(filters);
    }

    @Override
    public int getID() {
        return ID;
    }
}
