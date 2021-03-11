package com.prio.kejaksaan.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.valdesekamdem.library.mdtoast.MDToast;

public class Calling {
    @SerializedName("error_code")
    public String error;
    @SerializedName("error_message")
    public String desc;

    public static boolean TreatResponse(Context context, String tag, @Nullable Calling data) {
        if (data != null)
            return data.TreatResponse(context, tag);
        else
            Log.i(tag, "onResponseError: " + "POST " + tag + " gagal");
        return false;
    }

    public boolean TreatResponse(Context context, String tag) {
        Log.i(tag, "Error          -->  " + error);
        Log.i(tag, "Description       -->  " + desc);
        if (error.equals("0")) {
            Log.i(tag, "Success : " + tag + " : " + desc);
            return true;
        } else {
            MDToast.makeText(context, desc, Toast.LENGTH_LONG, MDToast.TYPE_ERROR).show();
            Log.e(tag, "Failed : \n Error " + error + " : " + desc);
            return false;
        }
    }
}