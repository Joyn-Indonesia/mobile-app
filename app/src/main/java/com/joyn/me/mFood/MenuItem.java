package com.joyn.me.mFood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David Studio on 12/31/2017.
 */

public class MenuItem extends AbstractItem<MenuItem, MenuItem.ViewHolder> {

    Context context;
    public int idMenu;
    public String menuMakanan;

    public MenuItem(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return com.joyn.me.R.id.menu_item;
    }

    @Override
    public void bindView(MenuItem.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.menuText.setText(menuMakanan);
    }

    @Override
    public int getLayoutRes() {
        return com.joyn.me.R.layout.item_foodmenu;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(com.joyn.me.R.id.menu_text)
        TextView menuText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
