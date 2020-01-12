package com.joyn.me.mFood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David Studio on 12/28/2017.
 */

public class KategoriItem extends AbstractItem<KategoriItem, KategoriItem.ViewHolder> {

    Context context;
    public int idKategori;
    public String kategori;
    public String image;

    public KategoriItem(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return com.joyn.me.R.id.kategori_item;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.kategoriName.setText(kategori);
        Glide.with(context).load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.kategoriIMG);
//        Picasso.with(context).load(image).fit().centerCrop().into(holder.kategoriIMG);
    }

    @Override
    public int getLayoutRes() {
        return com.joyn.me.R.layout.item_kategori;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(com.joyn.me.R.id.kategori_img)
        ImageView kategoriIMG;

        @BindView(com.joyn.me.R.id.kategori_name)
        TextView kategoriName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
