package com.joyn.me.model.json.book;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.joyn.me.model.AdditionalMbox;

/**
 * Created by David Studio on 12/23/2017.
 */

public class GetAdditionalMboxResponseJson {

    @Expose
    @SerializedName("message")
    public String message;

    @Expose
    @SerializedName("data")
    public AdditionalMbox data;

}
