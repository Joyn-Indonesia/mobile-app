package com.joyn.me.model.json.book.massage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by David Studio on 1/12/2017.
 */

public class DetailTransaksiRequest {

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