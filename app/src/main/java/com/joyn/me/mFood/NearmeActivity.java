package com.joyn.me.mFood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.BookService;
import com.joyn.me.model.DataRestoran;
import com.joyn.me.model.User;
import com.joyn.me.utils.Log;

import com.joyn.me.model.KategoriRestoran;
import com.joyn.me.model.Restoran;
import com.joyn.me.model.json.book.GetDataRestoRequestJson;
import com.joyn.me.model.json.book.GetDataRestoResponseJson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearmeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;

    @BindView(com.joyn.me.R.id.food_search)
    LinearLayout foodSearch;

    @BindView(com.joyn.me.R.id.nearme_recycler)
    RecyclerView nearmeRecycler;

    @BindView(com.joyn.me.R.id.nearme_noRecord)
    TextView noRecord;

    @BindView(com.joyn.me.R.id.nearme_progress)
    ProgressBar progress;

    private Realm realm;
    private RealmResults<Restoran> restoranRealmResults;
    private FastItemAdapter<RestoranItem> restoranAdapter;

    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private boolean requestUpdate = true;
    private List<KategoriRestoran> kategoriRestoran;
    private List<Restoran> restoran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_nearme);
        ButterKnife.bind(this);

        showRecycler();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        realm = Realm.getDefaultInstance();

        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearmeActivity.this, SearchRestoranActivity.class);
                NearmeActivity.this.startActivity(intent);
            }
        });

        restoranAdapter = new FastItemAdapter<>();
        if (requestUpdate) {
            nearmeRecycler.setLayoutManager(new LinearLayoutManager(this));
            nearmeRecycler.setAdapter(restoranAdapter);
            restoranAdapter.withOnClickListener(new FastAdapter.OnClickListener<RestoranItem>() {
                @Override
                public boolean onClick(View v, IAdapter<RestoranItem> adapter, RestoranItem item, int position) {
                    Log.e("BUTTON", "CLICKED");
                    Restoran selectedResto = realm.where(Restoran.class).
                            equalTo("id", restoranAdapter.getAdapterItem(position).id).findFirst();
                    Intent intent = new Intent(NearmeActivity.this, FoodMenuActivity.class);
                    intent.putExtra(FoodMenuActivity.ID_RESTO, selectedResto.getId());
                    intent.putExtra(FoodMenuActivity.NAMA_RESTO, selectedResto.getNamaResto());
                    intent.putExtra(FoodMenuActivity.ALAMAT_RESTO, selectedResto.getAlamat());
                    intent.putExtra(FoodMenuActivity.DISTANCE_RESTO, selectedResto.getDistance());
                    intent.putExtra(FoodMenuActivity.JAM_BUKA, selectedResto.getJamBuka());
                    intent.putExtra(FoodMenuActivity.JAM_TUTUP, selectedResto.getJamTutup());
                    intent.putExtra(FoodMenuActivity.IS_OPEN, selectedResto.isOpen());
                    intent.putExtra(FoodMenuActivity.PICTURE_URL, selectedResto.getFotoResto());
                    intent.putExtra(FoodMenuActivity.IS_MITRA, selectedResto.isPartner());
                    startActivity(intent);
                    return true;
                }
            });

            //TODO: Status restoran kerja sama dengan m-food, jam buka - tutup
            restoranRealmResults = realm.where(Restoran.class).findAll();
            RestoranItem restoranItem;
            for (int i = 0; i < restoranRealmResults.size(); i++) {
                restoranItem = new RestoranItem(NearmeActivity.this);
                restoranItem.id = restoranRealmResults.get(i).getId();
                restoranItem.namaResto = restoranRealmResults.get(i).getNamaResto();
                restoranItem.alamat = restoranRealmResults.get(i).getAlamat();
                restoranItem.distance = restoranRealmResults.get(i).getDistance();
                restoranItem.jamBuka = restoranRealmResults.get(i).getJamBuka();
                restoranItem.jamTutup = restoranRealmResults.get(i).getJamTutup();
                restoranItem.fotoResto = restoranRealmResults.get(i).getFotoResto();
                restoranItem.isOpen = restoranRealmResults.get(i).isOpen();
                restoranItem.pictureUrl = restoranRealmResults.get(i).getFotoResto();
                restoranItem.isMitra = restoranRealmResults.get(i).isPartner();
                restoranAdapter.add(restoranItem);
                Log.e("RESTO", restoranItem.namaResto + "");
                Log.e("RESTO", restoranItem.alamat + "");
                Log.e("RESTO", restoranItem.jamBuka + "");
                Log.e("RESTO", restoranItem.jamTutup + "");
                Log.e("RESTO", restoranItem.fotoResto + "");
            }
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();

    }


    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (lastKnownLocation != null) {
            if (requestUpdate) {
                getDataRestoran();


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation();
            } else {
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }
    }

    private void getDataRestoran() {
        if (lastKnownLocation != null) {
            User loginUser = GoridemeAplication.getInstance(this).getLoginUser();
            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetDataRestoRequestJson param = new GetDataRestoRequestJson();
            param.setLatitude(lastKnownLocation.getLatitude());
            param.setLongitude(lastKnownLocation.getLongitude());

            service.getDataRestoran(param).enqueue(new Callback<GetDataRestoResponseJson>() {
                @Override
                public void onResponse(Call<GetDataRestoResponseJson> call, Response<GetDataRestoResponseJson> response) {
                    if (response.isSuccessful()) {
                        DataRestoran dataRestoran = response.body().getDataRestoran();
                        kategoriRestoran = dataRestoran.getKategoriRestoranList();
                        restoran = dataRestoran.getRestoranList();
                        Log.d(FoodActivity.class.getSimpleName(), "Number of kategori: " + kategoriRestoran.size());
                        Log.d(FoodActivity.class.getSimpleName(), "Number of restoran: " + restoran.size());
                        Realm realm = GoridemeAplication.getInstance(NearmeActivity.this).getRealmInstance();
                        realm.beginTransaction();
                        realm.delete(KategoriRestoran.class);
                        realm.copyToRealm(kategoriRestoran);
                        realm.commitTransaction();

                        realm.beginTransaction();
                        realm.delete(Restoran.class);
                        realm.copyToRealm(restoran);
                        realm.commitTransaction();
                    }
                }

                @Override
                public void onFailure(Call<GetDataRestoResponseJson> call, Throwable t) {
//                    Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.",
//                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showRecycler() {
        nearmeRecycler.setVisibility(View.VISIBLE);
        noRecord.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
    }

    private void showNoRecord() {
        nearmeRecycler.setVisibility(View.GONE);
        noRecord.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void showProgress() {
        nearmeRecycler.setVisibility(View.GONE);
        noRecord.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestUpdate = true;
    }
}
