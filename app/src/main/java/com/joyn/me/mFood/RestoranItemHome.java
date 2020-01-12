package com.joyn.me.mFood;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import com.joyn.me.utils.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class RestoranItemHome extends AbstractItem<RestoranItemHome, RestoranItemHome.ViewHolder> {

    Context context;
    public int id;
    public String namaResto;
    public String alamat;
    public double distance;
    public String jamBuka;
    public String jamTutup;
    public String fotoResto;
    public boolean isOpen;
    public boolean isMitra;
    public String pictureUrl;

    public RestoranItemHome(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return com.joyn.me.R.id.nearme_item;
    }

    @Override
    public void bindView(RestoranItemHome.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.nearmeResto.setText(namaResto);
        NumberFormat formatter = new DecimalFormat("#0.00");
        Glide.with(context).load(fotoResto).asBitmap()
                .animate(com.joyn.me.R.anim.abc_fade_in).into(holder.nearmeIMG);
        holder.nearmeInfo.setText(formatter.format(distance) + " KM");

        boolean isActuallyOpen = false;

        if (isOpen) {
            isActuallyOpen = calculateJamBukaTutup();
        } else {
            isActuallyOpen = false;
        }

        holder.closed.setVisibility(isActuallyOpen ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getLayoutRes() {
        return com.joyn.me.R.layout.item_nearme_home;
    }

    private boolean calculateJamBukaTutup() {
        String[] parsedJamBuka = jamBuka.split(":");
        String[] parsedJamTutup = jamTutup.split(":");

        int jamBuka = Integer.parseInt(parsedJamBuka[0]), menitBuka = Integer.parseInt(parsedJamBuka[1]);
        int jamTutup = Integer.parseInt(parsedJamTutup[0]), menitTutup = Integer.parseInt(parsedJamTutup[1]);

        int totalMenitBuka = (jamBuka * 60) + menitBuka;
        int totalMenitTutup = (jamTutup * 60) + menitTutup;

        Calendar now = Calendar.getInstance();
        int totalMenitNow = (now.get(Calendar.HOUR_OF_DAY) * 60) + now.get(Calendar.MINUTE);

        Log.d("RestoranItem", "NOW HOUR = " + now.get(Calendar.HOUR_OF_DAY) + " Now Minutes ");

        Log.d("RestoranItem", "totalMenitBuka = " + totalMenitBuka + ", totalMenitTutup = " + totalMenitTutup + ", totalMenitNow = " + totalMenitNow);

        return totalMenitNow <= totalMenitTutup && totalMenitNow >= totalMenitBuka;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(com.joyn.me.R.id.nearme_img)
        ImageView nearmeIMG;

        @BindView(com.joyn.me.R.id.nearme_resto)
        TextView nearmeResto;

        @BindView(com.joyn.me.R.id.nearme_closed)
        TextView closed;

        @BindView(com.joyn.me.R.id.nearme_address)
        TextView nearmeInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

}
