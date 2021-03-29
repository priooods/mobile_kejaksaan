package com.prio.kejaksaan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prio.kejaksaan.databinding.ModelListUsersBinding;
import com.prio.kejaksaan.model.UserModel;

import java.util.List;

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
        holder.binding.access.setText(models.get(position).type);
        if (models.get(position).avatar != null){
            Glide.with(context).load("https://digitalsystemindo.com/jaksa/public/images/" + models.get(position).avatar)
                    .circleCrop().into(holder.binding.avatar);
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