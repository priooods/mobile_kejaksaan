package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.util.List;

public class PerkaraModel extends Calling {

    public static PerkaraModel i;
    public static boolean isExist(){
        return PerkaraModel.i != null;
    }

    public String penahanan;
    public String dakwaan;
    public String identitas;
    public String jenis;
    public String tanggal;
    public String updated_at;
    public String nomor;
    public String hari;
    public String tipe;
    public String agenda;
    public String surat_tugas;
    public String daftar_pengantar;
    public String fullname_jurusita;
    public String fullname_pp;
    public String message;
    public int pp;
    public int id;
    public int perkara_id;
    public int jurusita;


    public PerkaraModel data;
    public List<PerkaraModel> perkara;


    public static List<PerkaraModel> perkaradiproses;
    public static List<PerkaraListModel.Item> listperkara;
    public static List<PerkaraModel> notifyJurusita;
    public static List<PerkaraModel> notifyPPK;


    public static int statusPerkara;
    public static int buatPerkaraShow;
    public static int updatePerkaraStatus;

    public PerkaraModel(String ns, String nd, String xd){
        this.fullname_pp = ns;
        this.penahanan = nd;
        this.dakwaan = xd;
    }
}
