package com.prio.kejaksaan.views.perkara;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.DialogAddPerkaraBinding;
import com.prio.kejaksaan.layer.Layer_Perkara;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.valdesekamdem.library.mdtoast.MDToast;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class AddPerkara extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullscreen);
    }

    DialogAddPerkaraBinding binding;
    List<UserModel> pp, jurusita ;
    List<String> ppstr, jurusitastr ;
    String datesValue;
    int idPP, idJurusita;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddPerkaraBinding.inflate(inflater,container,false);

        binding.tanggal.setOnClickListener(v -> ShowDateTime(binding.tanggal));
        Log.i(TAG, "status Perkara: " + BaseModel.StatusPerkara);

        //filter type
        pp = new ArrayList<>();
        jurusita = new ArrayList<>();
        ppstr = new ArrayList<>();
        jurusitastr = new ArrayList<>();



        binding.back.setOnClickListener(v -> Objects.requireNonNull(getDialog()).dismiss());

        binding.pp.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapterPP = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, ppstr);
        binding.pp.setAdapter(adapterPP);
        binding.pp.setOnItemClickListener((parent, view, position, id) -> idPP = pp.get(position).id);

        binding.jurusita.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapterJurusita = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, jurusitastr);
        binding.jurusita.setAdapter(adapterJurusita);
        binding.jurusita.setOnItemClickListener((parent, view, position, id) -> idJurusita = jurusita.get(position).id);

        if (PerkaraModel.buatPerkaraShow == 2) {
            binding.dakwaan.setText(PerkaraModel.i.dakwaan);
            binding.identitas.setText(PerkaraModel.i.identitas);
            binding.nomerDakwaan.setText(PerkaraModel.i.nomor);
            binding.jenis.setText(PerkaraModel.i.jenis);
            binding.penahan.setText(PerkaraModel.i.penahanan);
            binding.tanggal.setText(PerkaraModel.i.tanggal);
            binding.btnCreatePerkara.setText("Update Perkara");
            binding.ppLayout.setVisibility(View.GONE);
            binding.typeLayout.setVisibility(View.GONE);
        }

        binding.btnCreatePerkara.setOnClickListener(v->{
            if (PerkaraModel.buatPerkaraShow == 2){
                UpdatePerkara();
            } else {
                TambahPerkara();
            }
        });
        return binding.getRoot();
    }

    private void ShowDateTime(TextInputEditText texit){
        Calendar calendar = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat fr = new SimpleDateFormat("YYYY/MM/dd", Locale.ENGLISH);
            SimpleDateFormat tex = new SimpleDateFormat("dd MMMM YYYY", Locale.ENGLISH);
            datesValue = fr.format(calendar.getTime());
            texit.setText(tex.format(calendar.getTime()));
        };

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.MONTH, -12);
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimaryDark));
        datePickerDialog.show(getChildFragmentManager(),"DateDialog");
    }

    public void UpdatePerkara(){
        Log.e("Perkara","Update Perkara!");
        Call<PerkaraModel> call = BaseModel.i.getService().UpdatePerkara(datesValue,
                Objects.requireNonNull(binding.nomerDakwaan.getText()).toString(),
                Objects.requireNonNull(binding.jenis.getText()).toString(), Objects.requireNonNull(binding.identitas.getText()).toString(),
                Objects.requireNonNull(binding.dakwaan.getText()).toString(), Objects.requireNonNull(binding.penahan.getText()).toString(),
                BaseModel.i.token, PerkaraModel.i.id);

        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Update Perkara", data)){
                    MDToast.makeText(requireContext(), "Perkara berhasil di update !", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new Layer_Perkara(),"perkara").commit();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void TambahPerkara(){
        Log.e("Perkara","Tambah Perkara!");
        Call<PerkaraModel> call = BaseModel.i.getService().AddPerkara(datesValue,
                Objects.requireNonNull(binding.nomerDakwaan.getText()).toString(),
                Objects.requireNonNull(binding.jenis.getText()).toString(), Objects.requireNonNull(binding.identitas.getText()).toString(),
                Objects.requireNonNull(binding.dakwaan.getText()).toString(), Objects.requireNonNull(binding.penahan.getText()).toString(),
                idPP,idJurusita, BaseModel.i.token);

        call.enqueue(new Callback<PerkaraModel>() {
            @Override
            public void onResponse(@NotNull Call<PerkaraModel> call, @NotNull Response<PerkaraModel> response) {
                PerkaraModel data = response.body();
                if (Calling.TreatResponse(requireContext(), "Create Perkara", data)){
                    assert data != null;
//                    PerkaraModel.listperkara.add(data.data);
                    MDToast.makeText(requireContext(), "Perkara Baru berhasil di buat", Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    assert getFragmentManager() != null;
                    Layer_Perkara perkara = (Layer_Perkara) getFragmentManager().findFragmentByTag("perkara");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert perkara != null;
                    transaction.detach(perkara);
                    transaction.attach(perkara);
                    transaction.commit();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<PerkaraModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

    public void GettingAllUsers(){
        Call<List<UserModel>> call = BaseModel.i.getService().AllUsers(BaseModel.i.token);
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<UserModel>> call, @NotNull Response<List<UserModel>> response) {
                List<UserModel> baseModel = response.body();
                assert baseModel != null;
                for (UserModel md : baseModel){
                    if (md.type.equals("PP")){
                        pp.add(md);
                        ppstr.add(md.fullname);
                    }
                }
                for (UserModel md : baseModel){
                    if (md.type.startsWith("Jurusita")){
                        jurusita.add(md);
                        jurusitastr.add(md.fullname);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<UserModel>> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        GettingAllUsers();
    }
}
