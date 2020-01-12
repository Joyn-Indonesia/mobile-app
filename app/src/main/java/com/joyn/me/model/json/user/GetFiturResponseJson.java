package com.joyn.me.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.joyn.me.model.DiskonMpay;
import com.joyn.me.model.Fitur;
import com.joyn.me.model.MfoodMitra;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Studio on 10/17/2017.
 */

public class GetFiturResponseJson {

    @Expose
    @SerializedName("data")
    private List<Fitur> data = new ArrayList<>();

    @Expose
    @SerializedName("diskon_mpay")
    private DiskonMpay diskonMpay;

    @Expose
    @SerializedName("mfood_mitra")
    MfoodMitra mfoodMitra;

    public List<Fitur> getData() {
        return data;
    }

    public void setData(List<Fitur> data) {
        this.data = data;
    }

    public DiskonMpay getDiskonMpay() {
        return diskonMpay;
    }

    public void setDiskonMpay(DiskonMpay diskonMpay) {
        this.diskonMpay = diskonMpay;
    }

    public MfoodMitra getMfoodMitra() {
        return mfoodMitra;
    }

    public void setMfoodMitra(MfoodMitra mfoodMitra) {
        this.mfoodMitra = mfoodMitra;
    }
}
