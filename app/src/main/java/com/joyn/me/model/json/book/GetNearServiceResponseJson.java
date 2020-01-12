package com.joyn.me.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.joyn.me.model.Driver;

import java.util.ArrayList;

/**
 * Created by David Studio on 12/21/2017.
 */

public class GetNearServiceResponseJson {

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
