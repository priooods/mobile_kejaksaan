package com.prio.kejaksaan.views.atk;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.RealPathUtil;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
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
    String[] prosessname;
    int idatk, prosesid;
    AdapterListPermintaan adapters;
    Intent openFileManager;
    public static int PRIVATE_CODE = 1;
    File files;


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
            case 2:
                binding.scn.setVisibility(View.GONE);
                binding.sco.setVisibility(View.VISIBLE);
                binding.uploadFile.setOnClickListener(v -> OpenManager());
                binding.btnUploadfile.setOnClickListener(v -> KirimATkLogVerify());
                binding.deleteFile.setOnClickListener(v -> {
                    binding.uploadFile.setVisibility(View.VISIBLE);
                    files = null;
                    binding.nameFile.setText(null);
                    binding.layoutNamefile.setVisibility(View.GONE);
                });
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


        if (AtkModel.atklist != null && AtkModel.atklist.size() != 0) {
            atkname = new String[AtkModel.atklist.size()];
        }
        int i;
        for (i=0; i<AtkModel.atklist.size();i++){
            atkname[i] = AtkModel.atklist.get(i).name;
        }

        //TODO: Bisa pake ini untuk call prosess id atau lainnya bebas bikin lagi
//        int i;
//        for (i=0; i<AtkModel.atklist.size();i++){
//            atkname[i] = AtkModel.atklist.get(i).name;
//        }

        binding.nameatk.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapternama = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, atkname);
        binding.nameatk.setAdapter(adapternama);
        binding.nameatk.setOnItemClickListener((parent, view, position, id) -> {
            idatk = AtkModel.atklist.get(position).id;
        });


        //TODO: Ini untuk permintaan prosess_id Atk barusan tadi
        //TODO: value dari text proses id itu (binding.prosesid)
        binding.prosesid.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapterprosesid = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, prosessname);
        binding.nameatk.setAdapter(adapterprosesid);
        binding.nameatk.setOnItemClickListener((parent, view, position, id) -> {
            //TODO: ini nanti di set proses id nya dan AtkMode.atklist diganti berdasarkan response
            prosesid = AtkModel.atklist.get(position).id;
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
                    MDToast.makeText(requireContext(), "ATK Baru berhasil di tambahkan", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
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
        binding.loading1.setVisibility(View.VISIBLE);
        HashMap<String, RequestBody> forms = new HashMap<>();
        int i = 0;
        for (AtkModel atk : atkModels){
            atk.getMappingBarangid(forms, i);
            atk.getMappingJumlah(forms, i);
            i++;
        }

        Call<AtkModel> call = BaseModel.i.getService().ReqATK(BaseModel.i.token, forms);
        call.enqueue(new Callback<AtkModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
                AtkModel data = response.body();
                if (Calling.TreatResponse(requireContext(),"Req ATK", data)){
                    MDToast.makeText(requireContext(),"Succesfully sending request ATK",Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    binding.loading1.setVisibility(View.GONE);
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
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void KirimATkLogVerify(){
        binding.loading2.setVisibility(View.VISIBLE);
        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("penyerahan", files.getPath(), File_form(files));
        }

        Call<AtkModel> call = BaseModel.i.getService().VerifyATKLog(BaseModel.i.token, AtkModel.i.id, fileToUpload);
        call.enqueue(new Callback<AtkModel>() {
            @Override
            public void onResponse(@NotNull Call<AtkModel> call, @NotNull Response<AtkModel> response) {
                AtkModel atkModel = response.body();
                if(Calling.TreatResponse(requireContext(),"req atk pp", atkModel)){
                    assert atkModel != null;
                    MDToast.makeText(requireContext(),"Success Verify Request ATK", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    binding.loading2.setVisibility(View.GONE);
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
                Log.e(TAG, "onFailure: ", t);
            }
        });
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

    public RequestBody File_form(File req) {
        return RequestBody.create(req, MediaType.parse("multipart/form-data"));
    }

    public void OpenManager(){
        openFileManager = new Intent(Intent.ACTION_GET_CONTENT);
        openFileManager.setType("image/*");
        startActivityForResult(openFileManager, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri path = data.getData();

                String filePath = RealPathUtil.getRealPathFromURI_API19(requireContext(), path);
                assert filePath != null;
                files = new File(filePath);

                String name = files.getName();
                int size = (int) files.length() / 1024;
                Log.i(TAG, "file: " + "name = " + name + " size = " + size);
                if (files != null){
                    binding.nameFile.setText(name);
                    binding.layoutNamefile.setVisibility(View.VISIBLE);
                    binding.uploadFile.setVisibility(View.GONE);
                }

            }
        }
    }

}
