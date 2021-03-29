package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.util.List;

public class DocumentModel extends Calling {

    public static DocumentModel i;
    public static boolean isExist(){
        return DocumentModel.i != null;
    }


    public int id;
    public String hari;
    public String tanggal;
    public String agenda;
    public int perkara_id;
    public String tipe;
    public String surat_tugas;
    public String daftar_pengantar;
    public int verifier_id;
    public String jenis;
    public String identitas;
    public String nomor;
    public String dakwaan;
    public String penahanan;
    public String fullname_pp;
    public String fullname;
    public String fullname_jurusita;
    public String tanggal_perkara;


    public List<DocumentModel> data;
    public DocumentModel surat;

    public static List<DocumentModel> semuasuratPanmud;
    public static List<DocumentModel> notifyJurusita;
    public static List<DocumentModel> semuaTugasJurusita;
    public static List<DocumentModel> semuaTugasPPK;
    public static List<PerkaraModel> perkaradiproses;
    public static int ShowDetailDocument;
}
