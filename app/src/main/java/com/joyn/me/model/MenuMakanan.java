package com.joyn.me.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;



public class MenuMakanan implements Serializable {

    @Expose
    @SerializedName("id_menu")
    private int idMenu;

    @Expose
    @SerializedName("menu_makanan")
    private String menuMakanan;

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public String getMenuMakanan() {
        return menuMakanan;
    }

    public void setMenuMakanan(String menuMakanan) {
        this.menuMakanan = menuMakanan;
    }
}
