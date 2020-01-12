package com.joyn.me.model.json.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by David Studio on 10/13/2017.
 */

public class ChangePasswordRequestJson {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("current_password")
    @Expose
    public String current_password;

    @SerializedName("new_password")
    @Expose
    public String new_password;


}
