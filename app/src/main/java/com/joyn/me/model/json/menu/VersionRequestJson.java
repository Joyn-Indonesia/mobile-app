package com.joyn.me.model.json.menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by David Studio on 12/1/2017.
 */

public class VersionRequestJson {
    @Expose
    @SerializedName("version")
    public String version;

    @Expose
    @SerializedName("application")
    public String application;

}
