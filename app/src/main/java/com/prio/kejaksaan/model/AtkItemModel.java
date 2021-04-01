package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AtkItemModel extends Calling {
    public List<Item> data;
    public class Item {
        public int id;
        public String name;
        public int jumlah;
        public String keterangan;
        public Timestamp created_at, updated_at;

        public Item updateJumlah(int total){
            jumlah = total;
            return this;
        }
        public RequestBody input_form(int value){
            return RequestBody.create(String.valueOf(value), MediaType.parse("multipart/form-data"));
        }
        public void getMapping(HashMap<String, RequestBody> map, int i){
            map.put("barang[" + i + "][barang_id]", input_form(id));
            map.put("barang[" + i + "][jumlah]", input_form(jumlah));
        }
    }
}