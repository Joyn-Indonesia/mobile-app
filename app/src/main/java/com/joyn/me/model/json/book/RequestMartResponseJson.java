package com.joyn.me.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.joyn.me.model.Transaksi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Studio on 12/7/2017.
 */

public class RequestMartResponseJson {

    @Expose
    @SerializedName("message")
    public String mesage;

    @Expose
    @SerializedName("data")
    public List<Transaksi> data = new ArrayList<>();

}
