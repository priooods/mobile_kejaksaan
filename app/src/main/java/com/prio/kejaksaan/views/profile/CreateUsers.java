package com.prio.kejaksaan.views.profile;

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

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.DialogCreateUsersBinding;
import com.prio.kejaksaan.model.BaseModel;
import com.prio.kejaksaan.service.Calling;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class CreateUsers extends DialogFragment {

    DialogCreateUsersBinding binding;
    String[] listType = { "SuperUser","Ketua","Panitera","KPA","Panmud","PP","Jurusita","PPK","Bendahara","Pengelola Persediaan"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCreateUsersBinding.inflate(inflater,container, false);

        binding.type.setDropDownBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.model_dropdown_input, R.id.dropdown_item, listType);
        binding.type.setAdapter(adapter);

        binding.btnCreateUsers.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.password.getText()).toString().isEmpty() || Objects.requireNonNull(binding.name.getText()).toString().isEmpty()
                    || Objects.requireNonNull(binding.fullname.getText()).toString().isEmpty() || Objects.requireNonNull(binding.type.getText()).toString().isEmpty()){
                MDToast.makeText(requireContext(),"Your Must Completed All Fields !", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
            } else if (Objects.requireNonNull(binding.password.getText()).toString().length() < 6){
                MDToast.makeText(requireContext(),"Password Min 6 Char !", Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
            } else {
                CreateNewUser(Objects.requireNonNull(binding.name.getText()).toString(),Objects.requireNonNull(binding.password.getText()).toString(),
                        Objects.requireNonNull(binding.fullname.getText()).toString(),Objects.requireNonNull(binding.type.getText()).toString());
            }
        });

        binding.backpress.setOnClickListener(v-> dismiss());

        return binding.getRoot();
    }

    private void CreateNewUser(String name, String pass, String fullname, String type){
        binding.progress.setVisibility(View.VISIBLE);
        Call<BaseModel> call = BaseModel.i.getService().CreateUser(name,fullname,pass,type);
        call.enqueue(new Callback<BaseModel>() {
            @Override
            public void onResponse(@NotNull Call<BaseModel> call, @NotNull Response<BaseModel> response) {
                BaseModel model = (BaseModel)response.body();
                if (Calling.TreatResponse(requireContext(),"CreateUsers", model)){
                    MDToast.makeText(requireContext(),"Successfully Create New Users", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    Objects.requireNonNull(getDialog()).cancel();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseModel> call, @NotNull Throwable t) {
                Log.e(TAG, "onFailure: ", t );
            }
        });
    }

}
