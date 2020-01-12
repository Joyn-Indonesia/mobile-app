package com.joyn.me.model.json.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.joyn.me.model.ItemHistory;

import java.util.ArrayList;

/**
 * Created by David Studio on 11/28/2017.
 */

public class HistoryResponseJson {
    @Expose
    @SerializedName("message")
    public String mesage;

    @Expose
    @SerializedName("data")
    public ArrayList<ItemHistory> data = new ArrayList<>();

}
