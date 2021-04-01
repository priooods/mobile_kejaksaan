package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.sql.Timestamp;
import java.util.List;

public class PerkaraListModel extends Calling {
    public static Item i;
    public List<Item> data;
    public class Item {
        public int id;
        public String tanggal;
        public String nomor;
        public String jenis;
        public String identitas;
        public String penahanan;
        public String dakwaan;
        public String fullname_pp;
        public String fullname_jurusita;
        public Proses proses;
        public String getIdentity(){
            return identitas+" - "+dakwaan;
        }
    }
    public class Proses{
        public int id;
        public String hari;
        public String tanggal;
        public String agenda;
        public int perkara_id;
        public String created;
        public String print(){
            return hari+","+tanggal+" : "+agenda;
        }
    }
}
