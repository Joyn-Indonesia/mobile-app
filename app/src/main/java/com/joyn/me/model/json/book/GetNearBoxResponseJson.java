package com.joyn.me.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.joyn.me.model.Driver;

import java.util.ArrayList;

/**
 * Created by David Studio on 10/17/2017.
 */

public class GetNearBoxResponseJson {

    @Expose
    @SerializedName("data")
    private ArrayList<Driver> data = new ArrayList<>();

    public ArrayList<Driver> getData() {
        return data;
    }

    public void setData(ArrayList<Driver> data) {
        this.data = data;
    }
}
