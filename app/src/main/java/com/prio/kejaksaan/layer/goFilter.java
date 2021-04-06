package com.prio.kejaksaan.layer;

import android.util.Log;
import android.widget.Filter;

import com.prio.kejaksaan.model.AtkRequest;
import com.prio.kejaksaan.model.PerkaraListModel;

import java.util.ArrayList;
import java.util.List;

public interface goFilter{
    public void Filter(CharSequence filters);
    public int getID();
}
//    @Override
//    public void Filter(CharSequence filters) {
//        if (adapterAtk != null)
//            adapterAtk.getFilter().filter(filters);
//    }


//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                if (constraint.length() == 0){
//                    Log.e("Request","No Adapter");
//                    FilterResults filterResults = new FilterResults();
//                    filterResults.values = unfilter;
//                    return filterResults;
//                }
//                String key = constraint.toString();
//                List<AtkRequest.Item> modelss = new ArrayList<>();
//                for (AtkRequest.Item model : unfilter) {
//                    PerkaraListModel.Item perkara = model.proses.perkara;
//                    if (model.proses.agenda.toLowerCase().contains(key.toLowerCase()) ||
//                            perkara.identitas.toLowerCase().contains(key.toLowerCase()) ||
//                            perkara.dakwaan.toLowerCase().contains(key.toLowerCase()) ||
//                            perkara.tanggal.toLowerCase().contains(key.toLowerCase()) ||
//                            perkara.jenis.toLowerCase().contains(key.toLowerCase()) ||
//                            perkara.nomor.toLowerCase().contains(key.toLowerCase())) {
//                        modelss.add(model);
//                    }
//                }
////                models = modelss;
//
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = modelss;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                models = (List<AtkRequest.Item>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }