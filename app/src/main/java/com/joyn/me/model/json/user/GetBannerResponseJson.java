package com.joyn.me.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.joyn.me.model.Banner;

import java.util.ArrayList;

/**
 * Created by David Studio on 10/17/2017.
 */

public class GetBannerResponseJson {

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public ArrayList<Banner> data;

}
