package com.joyn.me.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.joyn.me.model.DataMservice;

/**
 * Created by David Studio on 12/18/2017.
 */

public class GetDataMserviceResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private DataMservice dataMservice;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataMservice getDataMservice() {
        return dataMservice;
    }

    public void setData(DataMservice dataMservice) {
        this.dataMservice = dataMservice;
    }

}
