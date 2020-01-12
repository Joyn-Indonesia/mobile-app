package com.joyn.me.model.json.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by David Studio on 12/1/2017.
 */

public class VersionResponseJson {
    @Expose
    @SerializedName("new_version")
    public String new_version;

    @Expose
    @SerializedName("message")
    public String message;

    @Expose
    @SerializedName("data")
    public String[] data;


}
