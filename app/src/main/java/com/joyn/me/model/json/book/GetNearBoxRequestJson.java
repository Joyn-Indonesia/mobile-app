package com.joyn.me.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by David Studio on 10/17/2017.
 */

public class GetNearBoxRequestJson {

    @Expose
    @SerializedName("latitude")
    public double latitude;

    @Expose
    @SerializedName("longitude")
    public double longitude;
    @Expose
    @SerializedName("kendaraan_angkut")
    public int kendaraan_angkut;


}
