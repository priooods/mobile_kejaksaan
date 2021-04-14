package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.tools.Laravel;

import java.sql.Timestamp;
import java.util.List;

public class PerkaraListModel extends Calling {
    public static Item i;
    public List<Item> data;
    public class Item {
        public int id;
        public String status;
        public String tanggal;
        public String nomor;
        public String jenis;
        public String identitas;
        public String fullname_pp;
        public String fullname_jurusita;
        public Proses proses;
        public List<Proses> proses_list;
//        public String getIdentity(){
//            return identitas;
//        }
        public String getInformation(){
            return "Perkara No."+nomor+" Jenis "+jenis+".";
        }
    }
    public class Proses{
        public int id;
        public String hari;
        public String tanggal;
        public String agenda;
        public String penahanan;
//        public String dakwaan;
//        public int perkara_id;
//        public String created;
        public PerkaraListModel.Item perkara;
        public String getIdentity(){
            return perkara.identitas;
        }
        public String print(){
            return "No."+perkara.nomor+"\n"+hari+", "+ Laravel.getShortDate(tanggal)+" : "+agenda;
        }
    }
}
