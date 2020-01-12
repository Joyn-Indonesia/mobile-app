package com.joyn.me.model.json.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Studio on 11/28/2017.
 */

public class HelpResponseJson {
    @Expose
    @SerializedName("message")
    public String mesage;

    @Expose
    @SerializedName("data")
    public List<String> data = new ArrayList<>();


}
