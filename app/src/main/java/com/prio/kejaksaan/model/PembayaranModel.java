package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PembayaranModel extends Calling {
    public List<Item> data;
    public class Item {
        public int id;
        public String fullname_ppk;
        public SuratModel.Item surat_tugas;
        public String created,updated;
        public String kuitansi, surat;
    }
}
