package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.util.List;

public class SuratModel extends Calling {
//    public static List<Item> i;
    public static Item i;
    public static int ShowDetailDocument;
    public List<Item> data;
    public class Item {
        public int id;
        public String tipe;
        public String created;
        public String daftar_time;
        public String verify_time;
        public int perkara_id;
        public String biaya;
        public String surat_tugas;
        public String daftar_pengantar;
        public String fullname_ppk;
        public Integer verifier_id;
        public PerkaraListModel.Item perkara;
        public PerkaraListModel.Proses proses;
        public PembayaranModel pembayaran;

        public Item(PerkaraListModel.Item perkara){
            this.perkara = perkara;
        }
    }
    public class Alone extends Calling{
        public Item data;
    }
}
