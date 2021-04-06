package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.util.List;

public class AtkRequest extends Calling {
    public List<Item> data;
    public class Item{
        public int id;
        public Integer proses_id,pp_id,ppk_id,log_id;
        public String penyerahan, penerimaan;
        public List<AtkItemModel.Item> barang;
        public PerkaraListModel.Proses proses;
    }
}
