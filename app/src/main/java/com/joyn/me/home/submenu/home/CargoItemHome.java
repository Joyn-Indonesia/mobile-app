package com.joyn.me.home.submenu.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class CargoItemHome extends AbstractItem<CargoItemHome, CargoItemHome.ViewHolder> {

    Context context;
    public int id;
    public String type;
    public long price;
    public String dimension;
    public String maxWeight;
    public int featureId;
    public String image;

    public CargoItemHome(Context context) {
        this.context = context;
    }

    @Override
    public int getType() {
        return com.joyn.me.R.id.cargo_type;
    }

//    @Override
//    public void unbindView(ViewHolder holder) {
//        super.unbindView(holder);
//        holder.cargoTitle.setText(null);
//        holder.cargoDimension.setText(null);
//        holder.cargoMaxWeight.setText(null);
//        holder.cargoId = 0;
//        holder.featureId = 0;
//        holder.cargoImage.setImageDrawable(null);
//    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.cargoTitle.setText(type);
//        holder.context = context;
//        holder.cargoId = this.id;
//        holder.featureId = this.featureId;
        Glide.with(context).load(image).asBitmap()
                .animate(com.joyn.me.R.anim.abc_fade_in).into(holder.cargoImage);

    }

    @Override
    public int getLayoutRes() {
        return com.joyn.me.R.layout.item_mbox_cargo_type_home;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(com.joyn.me.R.id.cargo_image)
        ImageView cargoImage;

        @BindView(com.joyn.me.R.id.cargo_title)
        TextView cargoTitle;

//        private Realm realm;
//        Context context;
//        int cargoId;
//        int featureId;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static final ViewHolderFactory<? extends CargoItemHome.ViewHolder> FACTORY = new CargoItemHome.ItemFactory();

    /**
     * our ItemFactory implementation which creates the ViewHolder for our adapter.
     * It is highly recommended to implement a ViewHolderFactory as it is 0-1ms faster for ViewHolder creation,
     * and it is also many many times more efficient if you define custom listeners on views within your item.
     */
    protected static class ItemFactory implements ViewHolderFactory<CargoItemHome.ViewHolder> {
        public CargoItemHome.ViewHolder create(View v) {
            return new CargoItemHome.ViewHolder(v);
        }
    }

    /**
     * return our ViewHolderFactory implementation here
     *
     * @return
     */
    @Override
    public ViewHolderFactory<? extends CargoItemHome.ViewHolder> getFactory() {
        return FACTORY;
    }
}
