package com.joyn.me.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by David Studio on 11/02/2017.
 */

public class CheckStatusTransaksiRequest {

    @Expose
    @SerializedName("id_transaksi")
    private String idTransaksi;

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(String idTransaksi) {
        this.idTransaksi = idTransaksi;
    }
}
