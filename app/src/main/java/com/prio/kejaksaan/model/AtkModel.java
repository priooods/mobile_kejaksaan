package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AtkModel extends Calling {

    public static AtkModel i;
    public static boolean isExist(){
        return AtkModel.i != null;
    }

    public String keterangan;
    public int jumlah;
    public String name;
    public int id;
    public int ppk_id;
    public int log_id;
    public String surat;
    public String kuitansi;
    public String fullname;
    public String message;

    public List<AtkModel> data;
    public List<AtkModel> barang;

    public AtkModel(int ids, int jmlah, String names){
        this.id = ids;
        this.jumlah = jmlah;
        this.name = names;
    }

    public RequestBody input_form(int value){
        return RequestBody.create(String.valueOf(value), MediaType.parse("multipart/form-data"));
    }
    public void getMappingBarangid(HashMap<String, RequestBody> map, int i){
        map.put("barang[" + i + "][barang_id]", input_form(id));
    }
    public void getMappingJumlah(HashMap<String, RequestBody> map, int i){
        map.put("barang[" + i + "][jumlah]", input_form(jumlah));
    }

    public static int StatusAddATK;
    public static List<AtkModel> atklist;
}
