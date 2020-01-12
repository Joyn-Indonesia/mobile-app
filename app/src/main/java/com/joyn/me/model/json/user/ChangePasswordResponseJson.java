package com.joyn.me.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Studio on 10/13/2017.
 */

public class ChangePasswordResponseJson {

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public List<String> data = new ArrayList<>();
}
