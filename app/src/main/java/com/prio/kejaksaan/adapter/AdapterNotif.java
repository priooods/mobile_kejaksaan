package com.prio.kejaksaan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prio.kejaksaan.R;
import com.prio.kejaksaan.databinding.ModelNotificationBinding;
import com.prio.kejaksaan.layer.Layer_Document;
import com.prio.kejaksaan.model.ModelNotification;
import com.prio.kejaksaan.model.PerkaraModel;
import com.prio.kejaksaan.views.perkara.DetailPerkara;

import java.util.ArrayList;
import java.util.List;

public class AdapterNotif extends RecyclerView.Adapter<AdapterNotif.vHolder> {

    Context context;
    List<ModelNotification.Item> modelList;

    public AdapterNotif(Context context, List<ModelNotification.Item> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public vHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vHolder(ModelNotificationBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull vHolder holder, int position) {
        holder.binding.titleNotif.setText(modelList.get(position).message);
        holder.binding.subtitleNotif.setText(modelList.get(position).detail);
        holder.binding.textNotif.setText(modelList.get(position).time);

        holder.binding.readmore.setOnClickListener(v -> {
            //TODO: Ini nanti bisa click go to apa atau bisa click full item tinggal ganti
            //TODO: jadi holder.binding.card.. dibawah sample kalau mau ke dialogfragment
//            PerkaraModel.i = models.get(position);
//            FragmentActivity frg = (FragmentActivity)(context);
//            FragmentManager mrg = frg.getSupportFragmentManager();
//            DialogFragment fragment = new DetailPerkara();
//            fragment.show(mrg,"Detail Perkara");

            //TODO: dibawah sample kalau mau ke fragment
//            AppCompatActivity sct = (AppCompatActivity)context;
//            Fragment fr = new Layer_Document();
//            sct.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, fr, "harus kasih tag. " +
//                    "karena ada beberapa di dialog yg back nya by tag. tag liat" +
//                    "tag liat di basehome yah yg menus swicth").commit();
        });
    }

    @Override
    public int getItemCount() {
        if (modelList!=null){
            return modelList.size();
        }
        return 0;
    }

    public static class vHolder extends RecyclerView.ViewHolder{
        ModelNotificationBinding binding;
        public vHolder(ModelNotificationBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
