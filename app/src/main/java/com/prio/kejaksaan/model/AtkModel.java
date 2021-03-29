package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.util.List;

public class AtkModel extends Calling {

    public static AtkModel i;
    public static boolean isExist(){
        return AtkModel.i != null;
    }

    public String keterangan;
    public int jumlah;
    public String name;
    public int id;

    public AtkModel(int ids, int jmlah, String names){
        this.id = ids;
        this.jumlah = jmlah;
        this.name = names;
    }

    public static int StatusAddATK;
    public static List<AtkModel> atklist;
}
