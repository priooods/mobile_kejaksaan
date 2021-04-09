package com.prio.kejaksaan.tools;

public class Laravel {
//    static final String[] nama_bulan = {
//            "ERROR","Januari","Februari","Maret","April","Mei","Juni","Juli", "Agustus","September","Oktober","November","Desember"
//    };
    static public String getDate(String raw){
        String tanggal = raw.substring(8);
        String bulan = raw.substring(5,7);
        switch(bulan){
            case "01": bulan = "Januari"; break;
            case "02": bulan = "Februari"; break;
            case "03": bulan = "Maret"; break;
            case "04": bulan = "April"; break;
            case "05": bulan = "Mei"; break;
            case "06": bulan = "Juni"; break;
            case "07": bulan = "Juli"; break;
            case "08": bulan = "Agustus"; break;
            case "09": bulan = "September"; break;
            case "10": bulan = "Oktober"; break;
            case "11": bulan = "November"; break;
            case "12": bulan = "Desember"; break;
        }
        String tahun = raw.substring(0,4);
        return tanggal+" "+bulan+" "+tahun;
    }
    static public String getShortDate(String raw){
        String tanggal = raw.substring(8);
        String bulan = raw.substring(5,7);
        switch(bulan){
            case "01": bulan = "Jan"; break;
            case "02": bulan = "Feb"; break;
            case "03": bulan = "Mar"; break;
            case "04": bulan = "Apr"; break;
            case "05": bulan = "Mei"; break;
            case "06": bulan = "Juni"; break;
            case "07": bulan = "Juli"; break;
            case "08": bulan = "Agust"; break;
            case "09": bulan = "Sept"; break;
            case "10": bulan = "Okt"; break;
            case "11": bulan = "Nov"; break;
            case "12": bulan = "Des"; break;
        }
        String tahun = raw.substring(0,4);
        return tanggal+" "+bulan+" "+tahun;
    }
    static public String getShortDateTime(String raw){
        return raw.substring(11,16) + "," +getShortDate(raw.substring(0,10));
    }
}
