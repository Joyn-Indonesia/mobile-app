package com.joyn.me.mMart;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class MartItem extends AbstractItem<MartItem, MartItem.ViewHolder> {

    private String namaProduk = "";
    private int quantity = 1;

    public String getNamaProduk() {
        return namaProduk;
    }

    public void setNamaProduk(String namaProduk) {
        this.namaProduk = namaProduk;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int getType() {
        return com.joyn.me.R.id.item_menu;
    }

    @Override
    public int getLayoutRes() {
        return com.joyn.me.R.layout.item_menu;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        final ViewHolder holderFinal = holder;

        holder.productName.setText(namaProduk);
        holder.productQuantity.setText(String.valueOf(quantity));
        holder.productName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                namaProduk = holderFinal.productName.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
                holder.productQuantity.setText(String.valueOf(quantity));
            }
        });

        holder.removeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeProduct();
                holder.productQuantity.setText(String.valueOf(quantity));
            }
        });
    }

    private void addProduct() {
        if (quantity + 1 <= 20) quantity++;

    }

    private void removeProduct() {
        if (quantity - 1 > 0) quantity--;
    }

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.productName.setText(null);
        holder.productQuantity.setText(null);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(com.joyn.me.R.id.item_name)
        EditText productName;

        @BindView(com.joyn.me.R.id.item_rightButton)
        TextView addProduct;

        @BindView(com.joyn.me.R.id.item_leftButton)
        TextView removeProduct;

        @BindView(com.joyn.me.R.id.item_quantity)
        TextView productQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
