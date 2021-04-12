package com.prio.kejaksaan.views.perkara;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.DialogDetailPerkaraBinding;
import com.prio.kejaksaan.layer.Layer_Perkara;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.model.MessageModel;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.Laravel;
import com.prio.kejaksaan.views.document.AddDocument;
import com.valdesekamdem.library.mdtoast.MDToast;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class DetailPerkara extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullscreen);
    }

    DialogDetailPerkaraBinding binding;
    String days, tanggals;
    PerkaraListModel.Item model;
    int status;
    public DetailPerkara(int status, PerkaraListModel.Item model){
        this.status = status;
        this.model = model;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogDetailPerkaraBinding.inflate(inflater,container,false);
        binding.backpress.setOnClickListener(v -> dismiss());
        switch (UserModel.i.type){
            case "SuperUser":
//            case "KPA":
            case "Panitera":
                if (model.proses == null){
                    binding.btnupdatePerkara.setVisibility(View.VISIBLE);
                    binding.btndeletePerkara.setVisibility(View.VISIBLE);
                    binding.btndeletePerkara.setOnClickListener(v -> DeletePerkara());
                    binding.btnupdatePerkara.setOnClickListener(v -> {
                        DialogFragment fragment = new AddPerkara(2, model, this);
                        fragment.show(requireActivity().getSupportFragmentManager(),"Update Perkara");
                    });
                }else{
                    if (status == 1){
                        binding.btnsuratCreate.setVisibility(View.VISIBLE);
                        binding.btnsuratCreate.setOnClickListener(v -> SelesaiPerkara());
                        binding.btnsuratCreate.setText("Proses Selesai");
                    }
                }
                break;
            case "Panmud":
                    binding.btnsuratCreate.setVisibility(View.VISIBLE);
                    binding.btnsuratCreate.setOnClickListener(v -> {
//                    SuratModel.i = models.get(position);
                        FragmentActivity frg = (FragmentActivity) (getContext());
                        FragmentManager mrg = frg.getSupportFragmentManager();
                        DialogFragment fragment = null;

                        fragment = new AddDocument(1, model);
                        fragment.show(mrg, "Detail Surat");
                    });
                break;
            case "PP":
                if (status == 1){
                    binding.btnprosesPerkara.setVisibility(View.VISIBLE);
                }
                break;
        }

        RefreshPerkara();


        binding.btnprosesPerkara.setOnClickListener(v -> {
            if (binding.r3.getVisibility() == View.VISIBLE){
                AddProsesPerkara();
            } else {
                binding.r3.setVisibility(View.VISIBLE);
                binding.r2.setVisibility(View.GONE);
                binding.btnCancelprosesPerkara.setVisibility(View.VISIBLE);
            }
        });
        binding.btnCancelprosesPerkara.setOnClickListener(v -> {
            binding.r3.setVisibility(View.GONE);
            binding.btnCancelprosesPerkara.setVisibility(View.GONE);
        });

        return binding.getRoot();
    }
    public void RefreshPerkara(){
        if (status == 2) {
            binding.status.setText("Sudah");
            binding.status.setTextColor(getResources().getColor(R.color.green));
        }
        if (model.proses != null){
            binding.r2.setVisibility(View.VISIBLE);
            binding.agenda.setText(model.proses.agenda);
//            binding.dakwaan.setVisibility(View.GONE);
            binding.penahanan.setText(model.proses.penahanan);
            binding.tanggalProsess.setText(model.proses.hari + ", " + Laravel.getDate(model.proses.tanggal));
        }

        binding.nama.setText(model.identitas);
        binding.jenisPerkara.setText(model.jenis);
        binding.backpress.setOnClickListener(v->dismiss());
        binding.nomor.setText(model.nomor);
        binding.tanggal.setText(Laravel.getDate(model.tanggal));
        switch (UserModel.i.type){
            case "PP":
            case "Jurusita":
                binding.l5.setVisibility(View.GONE);
                binding.l6.setVisibility(View.GONE);
                break;
        }
        binding.ppName.setText(model.fullname_pp);
        binding.jurusitaName.setText(model.fullname_jurusita);

        binding.addTanggalProsess.setOnClickListener(v -> ShowDateTime(binding.addTanggalProsess));
    }

    public void DeletePerkara(){
        Call<MessageModel> call = BaseModel.i.getService().DeletePerkara(model.id);
        call.enqueue(new Callback<MessageModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel data = response.body();
                if (Calling.TreatResponse(requireContext(),"Delete Perkara", data)){
                    assert data != null;
                    MDToast.makeText(requireContext(), data.data, Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
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
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
    public void SelesaiPerkara(){
        Call<MessageModel> call = BaseModel.i.getService().ProsesSelesai(BaseModel.i.token,model.id);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel data = response.body();
                if (Calling.TreatResponse(requireContext(),"Selesai Perkara", data)){
                    assert data != null;
                    MDToast.makeText(requireContext(), data.data, Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
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
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void AddProsesPerkara(){
//        Log.e("PERKARA",days);
//        return;
        Call<MessageModel> call = BaseModel.i.getService().PerkaraAddProses(BaseModel.i.token,tanggals,
                String.valueOf(model.id),
                days, Objects.requireNonNull(binding.addAgenda.getText()).toString(),
               Objects.requireNonNull(binding.addPenahanan.getText()).toString()
                );
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                MessageModel data = response.body();
                if (Calling.TreatResponse(requireContext(),"Add Proses", data)){
                    Layer_Perkara perkara = (Layer_Perkara) getFragmentManager().findFragmentByTag("perkara");
                    FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                    assert perkara != null;
                    transaction.detach(perkara);
                    transaction.attach(perkara);
                    transaction.commit();
                    MDToast.makeText(requireContext(), data.data, Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    private void ShowDateTime(TextInputEditText texit){
        Calendar calendar = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat fr = new SimpleDateFormat("YYYY/MM/dd", Locale.ENGLISH);
            SimpleDateFormat tex = new SimpleDateFormat("dd MMMM YYYY", Locale.ENGLISH);
            SimpleDateFormat hari = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            days = hari.format(calendar.getTime());
            switch(days){
                case "Monday": days = "Senin"; break;
                case "Tuesday" : days = "Selasa"; break;
                case "Wednesday" : days = "Rabu"; break;
                case "Thursday" : days = "Kamis"; break;
                case "Friday" : days = "Jumat"; break;
                case "Saturday" : days = "Sabtu"; break;
                case "Sunday" : days = "Minggu"; break;
            }
            tanggals = fr.format(calendar.getTime());
            texit.setText(tex.format(calendar.getTime()));
        };

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.add(Calendar.MONTH, -12);
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimaryDark));
        datePickerDialog.show(getChildFragmentManager(),"DateDialog");
    }
}
