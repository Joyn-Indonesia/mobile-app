package com.joyn.me.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;



public class RestoranFoodSearchResult extends AbstractItem<RestoranFoodSearchResult, RestoranFoodSearchResult.ViewHolder> {

    @Expose
    @SerializedName("id_restoran")
    private int idRestoran;

    @Expose
    @SerializedName("nama")
    private String nama;

    @Expose
    @SerializedName("kategori")
    private String kategori;

    @Expose
    @SerializedName("distance")
    private double distance;

    public int getIdRestoran() {
        return idRestoran;
    }

    public void setIdRestoran(int idRestoran) {
        this.idRestoran = idRestoran;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int getType() {
        return com.joyn.me.R.id.restoranFoodSearchResult;
    }

    @Override
    public int getLayoutRes() {
        return com.joyn.me.R.layout.item_restoran_food_search_result;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.content.setText(toTitleCase(nama));
        holder.description.setText(toTitleCase(kategori));
        holder.distance.setText(String.format(Locale.US, "%.2f km", distance));
    }

    private static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(com.joyn.me.R.id.restoranFoodSearchResult_content)
        TextView content;

        @BindView(com.joyn.me.R.id.restoranFoodSearchResult_description)
        TextView description;

        @BindView(com.joyn.me.R.id.restoranFoodSearchResult_distance)
        TextView distance;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
