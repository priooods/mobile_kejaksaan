package com.prio.kejaksaan.views.atk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.prio.kejaksaan.model.AtkItemModel;
import com.prio.kejaksaan.model.AtkModel;
import com.prio.kejaksaan.model.AtkRequest;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.MessageModel;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
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

    ArrayList<AtkItemModel.Item> atkModels;
    AtkItemModel.Item atkItem;
    AtkRequest.Item atkRequest;
    int prosesId = -1;
    DialogAddAtkBinding binding;
    List<AtkItemModel.Item> atkid;
    int[] prosesid;
    String[] atkname;
    String[] prosessname;
    AdapterListPermintaan adapters;
    Intent openFileManager;
    public static int PRIVATE_CODE = 1;
    File files;
    int mode;

    public AddATK(int mode){
        this.mode = mode;
    }
    public AddATK(int mode, AtkRequest.Item item){
        atkRequest = item;
        this.mode = mode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddAtkBinding.inflate(inflater,container,false);
        binding.backpress.setOnClickListener(v -> dismiss());
        atkModels = new ArrayList<>();
        switch (mode){
            case 0:
                binding.btnCreateUsers.setOnClickListener(v->TambahATK());
                break;
            case 1:
                binding.title.setText("Buat Permintaan ATK");
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
            case 3:
                binding.scn.setVisibility(View.GONE);
                binding.sco.setVisibility(View.VISIBLE);
                binding.uploadFile.setOnClickListener(v -> OpenManager());
                binding.btnUploadfile.setOnClickListener(v -> KirimAtkPPVerify());
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

        listATK();
        listProses();
        binding.nameatk.setOnFocusChangeListener((view, b) -> {
            if (atkid == null)
                return;
            atkname = new String[atkid.size()];
            for (int i=0; i<atkid.size();i++){
                atkname[i] = atkid.get(i).name;
            }
            binding.nameatk.setDropDownBackgroundDrawable(new ColorDrawable(AddATK.this.getResources().getColor(R.color.colorPrimary)));
            ArrayAdapter<String> adapternama = new ArrayAdapter<>(AddATK.this.requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, atkname);
            binding.nameatk.setAdapter(adapternama);
            binding.nameatk.setOnItemClickListener((parent, eview, position, id) -> {
                atkItem = atkid.get(position);
                binding.nameatk.clearFocus();
            });
        });
        binding.prosesid.setOnFocusChangeListener((view,b) -> {
            if (prosessname !=null) {
                binding.prosesid.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                ArrayAdapter<String> adapterprosesid = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, prosessname);
                binding.prosesid.setAdapter(adapterprosesid);
                binding.prosesid.setOnItemClickListener((eparent, eview, position, eid) -> {
                    //TODO: ini nanti di set proses id nya dan AtkMode.atklist diganti berdasarkan response
                    prosesId = prosesid[position];
                    Log.e("AddATK","Proses id = "+prosesId);
                    binding.prosesid.clearFocus();
                });
            }
        });
        binding.atkprosesidLayout.setVisibility(View.GONE);

        binding.btnAddMinta.setOnClickListener(v -> {
            if (binding.jumlahminta.getText().toString().equals("")) {
//                Toast.makeText(requireContext(),"a",1,"Masukan besar jumlah ATK!");
                Toast.makeText(requireContext(),"Masukan besar jumlah ATK!",Toast.LENGTH_SHORT).show();
                return;
            }else
                if (atkItem == null){
                    Toast.makeText(requireContext(),"Masukan Nama ATK dengan benar!",Toast.LENGTH_SHORT).show();
                    return;
            }
            atkModels.add(atkItem.updateJumlah(Integer.parseInt(Objects.requireNonNull(binding.jumlahminta.getText()).toString())));
            atkid.remove(atkItem);
            atkItem = null;
            setListPermintaan(atkModels);
            binding.btnCreateUsers.setVisibility(View.VISIBLE);
            binding.formMinta.setVisibility(View.GONE);
            binding.nameatk.setText(null);
            binding.jumlahminta.setText(null);
        });

        return binding.getRoot();
    }

    public void setListPermintaan(ArrayList<AtkItemModel.Item> mod){
        if (atkModels != null && atkModels.size() != 0){
            adapters = new AdapterListPermintaan(requireContext(), mod);
            binding.listAtk.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            binding.listAtk.setAdapter(adapters);
            adapters.notifyDataSetChanged();
        }
    }

    public void TambahATK(){
        Log.e("ATK","TAMBAH ATK");
        String name = Objects.requireNonNull(binding.name.getText()).toString();
        String jumlah = Objects.requireNonNull(binding.jumlah.getText()).toString();
        Call<MessageModel> call = BaseModel.i.getService().AddATK(
                (name.equals("") ? null:name),
                Objects.requireNonNull(binding.keterangan.getText()).toString(),
                (jumlah.equals("") ? null:Integer.parseInt(jumlah))
        );

        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel data = response.body();
                if (Calling.TreatResponse(getContext(), "Add ATK", data)){
                    assert data != null;
                    MDToast.makeText(getContext(), data.data, Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
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
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t );
            }
        });
    }

    public void listATK(){
        Call<List<AtkItemModel.Item>> call = BaseModel.i.getService().AtkList();
        call.enqueue(new Callback<List<AtkItemModel.Item>>() {
            @Override
            public void onResponse(@NotNull Call<List<AtkItemModel.Item>> call, @NotNull Response<List<AtkItemModel.Item>> response) {
                    List<AtkItemModel.Item> data = response.body();
                    if (data == null)
                        return;
                    atkid = data;
                    Log.e("ATKREQ","List Acquired "+atkid.size());
            }

            @Override
            public void onFailure(@NotNull Call<List<AtkItemModel.Item>> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t );
            }
        });
    }
    public void listProses(){
        Call<List<PerkaraListModel.Proses>> call = BaseModel.i.getService().PerkaraProsesPP(BaseModel.i.token);
        call.enqueue(new Callback<List<PerkaraListModel.Proses>>() {
            @Override
            public void onResponse(@NotNull Call<List<PerkaraListModel.Proses>> call, @NotNull Response<List<PerkaraListModel.Proses>> response) {
                List<PerkaraListModel.Proses> data = response.body();
                if (data == null) {
                    Log.e("Proses Perkara","Proses Data is null!"+response);
                    return;
                }
                Log.e("Proses Perkara","Proses Perkara "+data.size()+" aquired!");
                prosessname = new String[data.size()];
                prosesid = new int[data.size()];
                if (prosesid.length > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        prosessname[i] = data.get(i).print();
                        Log.e("Proses Id = " + i, prosessname[i]);
                        prosesid[i] = data.get(i).id;
                    }
                    binding.atkprosesidLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<PerkaraListModel.Proses>> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t );
            }
        });
    }

    public void KirimReqATK(){
        HashMap<String, RequestBody> forms = new HashMap<>();
        if (prosesId <0) {
            MDToast.makeText(requireContext(),"Select Proses Perkara!",Toast.LENGTH_LONG, MDToast.TYPE_WARNING).show();
            return;
        }
        binding.loading1.setVisibility(View.VISIBLE);
        int i = 0;
        for (AtkItemModel.Item atk : atkModels){
            atk.getMapping(forms, i);
            i++;
        }

        Call<MessageModel> call = BaseModel.i.getService().ReqATK(BaseModel.i.token, prosesId, forms);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel data = response.body();
                if (Calling.TreatResponse(requireContext(),"Req ATK", data)){
                    MDToast.makeText(requireContext(),data.data,Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
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
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void KirimAtkPPVerify(){
        binding.loading2.setVisibility(View.VISIBLE);
        MultipartBody.Part fileToUpload = null;
        if (files != null){
            fileToUpload = MultipartBody.Part.createFormData("penerimaan", files.getPath(), File_form(files));
        }

        Call<MessageModel> call = BaseModel.i.getService().AtkaccPP(BaseModel.i.token, atkRequest.id, fileToUpload);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel atkModel = response.body();
                if(Calling.TreatResponse(requireContext(),"req atk pp", atkModel)){
                    assert atkModel != null;
                    MDToast.makeText(requireContext(),atkModel.data, Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Persediaan persediaan = (Layer_Persediaan) getFragmentManager().findFragmentByTag("persediaan");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert persediaan != null;
                    atkRequest.penyerahan = files.getParent();
                    transaction.detach(persediaan);
                    transaction.attach(persediaan);
                    transaction.commit();
                    dismiss();
                }
                binding.loading2.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
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

        Call<MessageModel> call = BaseModel.i.getService().VerifyATKLog(BaseModel.i.token, atkRequest.id, fileToUpload);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel atkModel = response.body();
                if(Calling.TreatResponse(requireContext(),"req atk pp", atkModel)){
                    assert atkModel != null;
                    MDToast.makeText(requireContext(),atkModel.data, Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Persediaan persediaan = (Layer_Persediaan) getFragmentManager().findFragmentByTag("persediaan");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert persediaan != null;
                    atkRequest.penyerahan = files.getParent();
                    transaction.detach(persediaan);
                    transaction.attach(persediaan);
                    transaction.commit();
                    dismiss();
                }else
                    MDToast.makeText(requireContext(),"Permintaan Gagal, Coba Lagi!", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
                binding.loading2.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                binding.loading2.setVisibility(View.GONE);
                MDToast.makeText(requireContext(),"Verifikasi Gagal", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
            }
        });
    }

    public class AdapterListPermintaan extends RecyclerView.Adapter<AdapterListPermintaan.vHolder>{

        Context context;
        List<AtkItemModel.Item> atkModelList;

        public AdapterListPermintaan(Context context, List<AtkItemModel.Item> atkModelList) {
            this.context = context;
            this.atkModelList = atkModelList;
        }

        @NonNull
        @Override
        public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new vHolder(ModelAtkReqBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull vHolder holder, int position) {
            AtkItemModel.Item mod = atkModelList.get(position);
            holder.binding.name.setText(mod.name);
            holder.binding.jumlah.setText(mod.jumlah + " Items - "+mod.keterangan);

            holder.binding.delete.setOnClickListener(v -> {
                atkid.add(atkModelList.get(position));
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
