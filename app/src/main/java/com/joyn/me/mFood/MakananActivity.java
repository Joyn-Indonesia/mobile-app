package com.joyn.me.mFood;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.joyn.me.model.Makanan;

import com.joyn.me.model.PesananFood;
import com.joyn.me.utils.Log;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MakananActivity extends AppCompatActivity implements MakananItem.OnCalculatePrice {

    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;

    @BindView(com.joyn.me.R.id.menu_title)
    TextView menuTitle;

    @BindView(com.joyn.me.R.id.makanan_recycler)
    RecyclerView makananRecycler;

    @BindView(com.joyn.me.R.id.price_container)
    CardView priceContainer;

    @BindView(com.joyn.me.R.id.qty_text)
    TextView qtyText;

    @BindView(com.joyn.me.R.id.cost_text)
    TextView costText;

    public static final String ID_MENU = "idMenu";
    public static final String NAMA_RESTO = "namaResto";
    private int idMenu;
    private String namaMenu;

    private FastItemAdapter<MakananItem> makananAdapter;
    private RealmResults<Makanan> makananRealmResults;
    private RealmResults<PesananFood> existingFood;
    private MakananItem makananItem;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_makanan);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        idMenu = intent.getIntExtra(ID_MENU, -3);
        namaMenu = intent.getStringExtra(NAMA_RESTO);

        realm = Realm.getDefaultInstance();

        makananAdapter = new FastItemAdapter<>();
        makananAdapter.notifyDataSetChanged();
        makananAdapter.withSelectable(true);
        makananAdapter.withItemEvent(new ClickEventHook<MakananItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof MakananItem.ViewHolder) {
                    return ((MakananItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<MakananItem> fastAdapter, MakananItem item) {
                Makanan selectedMakanan = realm.where(Makanan.class).equalTo("id", makananAdapter.getAdapterItem(position).id).findFirst();
                Log.e("BUTTON", "CLICKED, ID : " + selectedMakanan.getId());
                final Dialog dialog = new Dialog(MakananActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(com.joyn.me.R.layout.dialog_header_polygon);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(true);
                TextView text = (TextView) dialog.findViewById(com.joyn.me.R.id.titledialog);
                text.setText(selectedMakanan.getNamaMenu());
                TextView textdesc = (TextView) dialog.findViewById(com.joyn.me.R.id.descdialog);
                textdesc.setText(selectedMakanan.getDeskripsiMenu());
                TextView textprice = (TextView) dialog.findViewById(com.joyn.me.R.id.pricedialog);
                textprice.setText("Price: $" + " " + String.valueOf(selectedMakanan.getHarga()));
                ImageView image = (ImageView) dialog.findViewById(com.joyn.me.R.id.image);
                Glide.with(MakananActivity.this).load(selectedMakanan.getFoto()).asBitmap()
                        .animate(com.joyn.me.R.anim.abc_fade_in).into(image);

                TextView dialogButton = (TextView) dialog.findViewById(com.joyn.me.R.id.bt_decline);
                /**
                 * Jika tombol diklik, tutup dialog
                 */
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        makananRecycler.setLayoutManager(new LinearLayoutManager(this));
        makananRecycler.setAdapter(makananAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        menuTitle.setText(namaMenu);

        priceContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakananActivity.this, BookingActivity.class);
                MakananActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadMakanan();
        calculatePrice();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void LoadMakanan() {
        makananAdapter.clear();

        makananRealmResults = realm.where(Makanan.class).
                equalTo("kategoriMenuMakanan", idMenu).findAll();
        existingFood = realm.where(PesananFood.class).findAll();

        for (int e = 0; e < existingFood.size(); e++) {
            existingFood.get(e).getIdMakanan();
            existingFood.get(e).getQty();
            Log.d("db id", existingFood.get(e).getIdMakanan() + " ");
            Log.d("db qty", existingFood.get(e).getQty() + " ");
        }

        int[] exiF = new int[makananRealmResults.size()];

        for (int i = 0; i < makananRealmResults.size(); i++) {
            makananItem = new MakananItem(MakananActivity.this, this);

            makananItem.quantity = 0;
            for (int j = 0; j < existingFood.size(); j++) {
                if (existingFood.get(j).getIdMakanan() == makananRealmResults.get(i).getId()) {
                    makananItem.quantity = existingFood.get(j).getQty();
                    makananItem.catatan = existingFood.get(j).getCatatan();
                    exiF[i] = existingFood.get(j).getQty();
                    Log.d("isi_exist_quantity", existingFood.get(j).getQty() + " list" + i);
                }
            }

            makananItem.id = makananRealmResults.get(i).getId();
            makananItem.namaMenu = makananRealmResults.get(i).getNamaMenu();
            makananItem.deskripsiMenu = makananRealmResults.get(i).getDeskripsiMenu();
            makananItem.foto = makananRealmResults.get(i).getFoto();
            makananItem.harga = makananRealmResults.get(i).getHarga();
            makananAdapter.add(makananItem);
        }

        for (int x = 0; x < exiF.length; x++) {
            Log.d("isi_x", exiF[x] + "");
        }

        makananAdapter.notifyDataSetChanged();
    }

    private void showDialogPolygon() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(com.joyn.me.R.layout.dialog_header_polygon);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);


        dialog.show();
    }

    @Override
    public void calculatePrice() {
        List<PesananFood> existingFood = realm.where(PesananFood.class).findAll();

        int quantity = 0;
        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += existingFood.get(p).getQty();
            cost += existingFood.get(p).getTotalHarga();
        }

        if (quantity > 0)
            priceContainer.setVisibility(View.VISIBLE);
        else
            priceContainer.setVisibility(View.GONE);

        qtyText.setText("" + quantity);
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(cost);
        String formattedText = String.format(Locale.US, "$. %s ,-", formattedTotal);
        costText.setText(formattedText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
