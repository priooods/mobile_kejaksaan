package com.prio.kejaksaan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ModelListUsersBinding;
import com.prio.kejaksaan.model.PerkaraListModel;
import com.prio.kejaksaan.model.UserModel;
import com.prio.kejaksaan.views.perkara.DetailPerkara;
import com.prio.kejaksaan.views.profile.EditProfile;

import java.util.List;

import static android.content.ContentValues.TAG;

public class AdapterAllUsers extends RecyclerView.Adapter<AdapterAllUsers.VHolder> {


    List<UserModel> models;
    Context context;

    public AdapterAllUsers(List<UserModel> models,Context context) {
        this.models = models;
        this.context = context;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHolder(ModelListUsersBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        holder.binding.username.setText(models.get(position).fullname);
        switch (models.get(position).type){
            case "KPA":
                holder.binding.access.setText("Kuasa Pengguna Anggaran");
                break;
            case "Panmud":
                holder.binding.access.setText("Panitera Muda");
                break;
            case "PP":
                holder.binding.access.setText("Panitera Pengganti");
                break;
            case "PPK":
                holder.binding.access.setText("Pejabat Pembuat Komitmen");
                break;
            default:
                holder.binding.access.setText(models.get(position).type);
                break;

        }
        if (models.get(position).avatar != null){
            Glide.with(context).load("https://digitalsystemindo.com/jaksa/public/images/" + models.get(position).avatar)
                    .circleCrop().into(holder.binding.avatar);
        }

        holder.binding.detailUserss.setBackground(context.getResources().getDrawable(R.color.shimer));
        if (UserModel.i.type.equals("KPA")) {
            holder.binding.detailUserss.setOnClickListener(v -> {
                UserModel.TypeCreateUser = 90;
                UserModel.i = models.get(position);
                FragmentActivity frg = (FragmentActivity) (context);
                FragmentManager mrg = frg.getSupportFragmentManager();
                DialogFragment fragment = new EditProfile();
                fragment.show(mrg, "Editing Profile");
            });
        }

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public static class VHolder extends RecyclerView.ViewHolder{
        ModelListUsersBinding binding;
        public VHolder(ModelListUsersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
