package com.joyn.me.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.joyn.me.model.DataRestoran;

/**
 * Created by David Studio on 12/28/2017.
 */

public class GetDataRestoResponseJson {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private DataRestoran dataRestoran;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataRestoran getDataRestoran() {
        return dataRestoran;
    }

    public void setDataRestoran(DataRestoran dataRestoran) {
        this.dataRestoran = dataRestoran;
    }
}
