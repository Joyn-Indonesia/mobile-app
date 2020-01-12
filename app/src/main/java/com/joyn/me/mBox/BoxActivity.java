package com.joyn.me.mBox;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.BookService;

import com.joyn.me.model.KendaraanAngkut;
import com.joyn.me.model.User;
import com.joyn.me.model.json.book.GetKendaraanAngkutResponseJson;
import com.joyn.me.utils.Log;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoxActivity extends AppCompatActivity {

//    @BindView(R.cargoId.button_pickup)
//    LinearLayout buttonPickup;
//
//    @BindView(R.cargoId.button_truck)
//    LinearLayout buttonTruck;

    @BindView(com.joyn.me.R.id.cargo_type_recyclerView)
    RecyclerView cargoTypeRecyclerView;
    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;

    public static final String FITUR_KEY = "FiturKey";
    public static final String CARGO = "cargo";

    private Realm realm;
    int featureId;
    FastItemAdapter<CargoItem> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_mbox);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        featureId = intent.getIntExtra(FITUR_KEY, -1);

        realm = Realm.getDefaultInstance();
        LoadKendaraan();
        itemAdapter = new FastItemAdapter<>();
        cargoTypeRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        cargoTypeRecyclerView.setAdapter(itemAdapter);

        itemAdapter.withItemEvent(new ClickEventHook<CargoItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CargoItem.ViewHolder) {
                    return ((CargoItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<CargoItem> fastAdapter, CargoItem item) {
                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", itemAdapter.getAdapterItem(position).id).findFirst();
                Log.e("BUTTON", "CLICKED, ID : " + selectedCargo.getIdKendaraan());
                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
                intent.putExtra(BoxOrder.FITUR_KEY, featureId);
                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
                startActivity(intent);
            }
        });

//        buttonPickup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", 4).findFirst();
//                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
//                intent.putExtra(BoxOrder.FITUR_KEY, featureId);
//                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
//                startActivity(intent);
//            }
//        });
//
//        buttonTruck.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", 5).findFirst();
//                Intent intent = new Intent(BoxActivity.this, BoxOrder.class);
//                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
//                startActivity(intent);
//            }
//        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void LoadKendaraan() {
        User loginUser = GoridemeAplication.getInstance(this).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.getKendaraanAngkut().enqueue(new Callback<GetKendaraanAngkutResponseJson>() {
            @Override
            public void onResponse(Call<GetKendaraanAngkutResponseJson> call, Response<GetKendaraanAngkutResponseJson> response) {
                if (response.isSuccessful()) {

                    Realm realm = GoridemeAplication.getInstance(BoxActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(KendaraanAngkut.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();


                    CargoItem cargoItem;
                    for (KendaraanAngkut cargo : response.body().getData()) {
                        cargoItem = new CargoItem(BoxActivity.this);
                        cargoItem.featureId = featureId;
                        cargoItem.id = cargo.getIdKendaraan();
                        cargoItem.type = cargo.getKendaraanAngkut();
                        cargoItem.price = cargo.getHarga();
                        cargoItem.image = cargo.getFotoKendaraan();
                        cargoItem.dimension = cargo.getDimensiKendaraan();
                        cargoItem.maxWeight = cargo.getMaxweightKendaraan();
                        itemAdapter.add(cargoItem);
                        Log.e("ADD CARGO", cargo.getIdKendaraan() + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<GetKendaraanAngkutResponseJson> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
