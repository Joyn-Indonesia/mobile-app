package com.joyn.me.mFood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.joyn.me.model.Fitur;
import com.joyn.me.model.User;
import com.joyn.me.utils.Log;

import com.joyn.me.mMart.MartActivity;
import com.joyn.me.model.KategoriRestoran;
import com.joyn.me.model.PromosiMFood;
import com.joyn.me.model.Restoran;
import com.joyn.me.model.json.book.GetDataRestoRequestJson;
import com.joyn.me.model.json.book.GetDataRestoResponseJson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import io.realm.Realm;
import io.realm.RealmResults;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;

    @BindView(com.joyn.me.R.id.food_search)
    LinearLayout foodSearch;

    @BindView(com.joyn.me.R.id.food_nearme)
    LinearLayout foodNearme;

    @BindView(com.joyn.me.R.id.food_manual)
    LinearLayout foodManual;

    @BindView(com.joyn.me.R.id.food_explore)
    RelativeLayout foodExplore;

    @BindView(com.joyn.me.R.id.text_explore)
    TextView textExplore;

    @BindView(com.joyn.me.R.id.show_more2)
    TextView showMore2;

    @BindView(com.joyn.me.R.id.slide_viewPager)
    AutoScrollViewPager autoScrollViewPager;

    @BindView(com.joyn.me.R.id.slide_viewPager_indicator)
    CircleIndicator circleIndicator;

    @BindView(com.joyn.me.R.id.kategori_recycler)
    RecyclerView kategoriRecyler;

    @BindView(com.joyn.me.R.id.nearme_recycler)
    RecyclerView nearmeRecyler;

    public static final String FITUR_KEY = "FiturKey";
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private boolean requestUpdate = true;
    private List<KategoriRestoran> kategoriRestoran;
    private List<Restoran> restoran;
    private FastItemAdapter<KategoriItem> kategoriAdapter;


    private Realm realm;
    private RealmResults<Restoran> restoranRealmResults;
    private FastItemAdapter<RestoranItemHome> restoranAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_food);
        ButterKnife.bind(this);
        showRecycler();
        realm = Realm.getDefaultInstance();

        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, SearchRestoranActivity.class);
                FoodActivity.this.startActivity(intent);
            }
        });

        restoranAdapter = new FastItemAdapter<>();

        nearmeRecyler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        nearmeRecyler.setAdapter(restoranAdapter);
        restoranAdapter.withOnClickListener(new FastAdapter.OnClickListener<RestoranItemHome>() {
            @Override
            public boolean onClick(View v, IAdapter<RestoranItemHome> adapter, RestoranItemHome item, int position) {
                Log.e("BUTTON", "CLICKED");
                Restoran selectedResto = realm.where(Restoran.class).
                        equalTo("id", restoranAdapter.getAdapterItem(position).id).findFirst();
                Intent intent = new Intent(FoodActivity.this, FoodMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        RestoranItemHome restoranItemHome;
        for (int i = 0; i < restoranRealmResults.size(); i++) {
            restoranItemHome = new RestoranItemHome(FoodActivity.this);
            restoranItemHome.id = restoranRealmResults.get(i).getId();
            restoranItemHome.namaResto = restoranRealmResults.get(i).getNamaResto();
            restoranItemHome.alamat = restoranRealmResults.get(i).getAlamat();
            restoranItemHome.distance = restoranRealmResults.get(i).getDistance();
            restoranItemHome.jamBuka = restoranRealmResults.get(i).getJamBuka();
            restoranItemHome.jamTutup = restoranRealmResults.get(i).getJamTutup();
            restoranItemHome.fotoResto = restoranRealmResults.get(i).getFotoResto();
            restoranItemHome.isOpen = restoranRealmResults.get(i).isOpen();
            restoranItemHome.pictureUrl = restoranRealmResults.get(i).getFotoResto();
            restoranItemHome.isMitra = restoranRealmResults.get(i).isPartner();
            restoranAdapter.add(restoranItemHome);
            Log.e("RESTO", restoranItemHome.namaResto + "");
            Log.e("RESTO", restoranItemHome.alamat + "");
            Log.e("RESTO", restoranItemHome.jamBuka + "");
            Log.e("RESTO", restoranItemHome.jamTutup + "");
            Log.e("RESTO", restoranItemHome.fotoResto + "");
        }
        getDataRestoran();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        realm = Realm.getDefaultInstance();
        kategoriAdapter = new FastItemAdapter<>();

        foodNearme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, NearmeActivity.class);
                startActivity(intent);
            }
        });

        foodManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 4).findFirst();
                Intent intent = new Intent(FoodActivity.this, MartActivity.class);
                intent.putExtra(MartActivity.FITUR_KEY, selectedFitur.getIdFitur());
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        foodSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, SearchRestoranActivity.class);
                FoodActivity.this.startActivity(intent);
            }
        });

        showMore2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodActivity.this, NearmeActivity.class);
                startActivity(intent);
            }
        });


        requestUpdate = true;


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
                KategoriRecycler();


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

    private void KategoriRecycler() {
        kategoriRecyler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        kategoriRecyler.setAdapter(kategoriAdapter);
        kategoriAdapter.withOnClickListener(new FastAdapter.OnClickListener<KategoriItem>() {
            @Override
            public boolean onClick(View v, IAdapter<KategoriItem> adapter, KategoriItem item, int position) {
                Log.e("BUTTON", "CLICKED");
                Intent intent = new Intent(FoodActivity.this, KategoriSelectActivity.class);
                intent.putExtra(KategoriSelectActivity.KATEGORI_ID, String.valueOf(item.idKategori));
                startActivity(intent);
                return true;
            }
        });
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
                        Realm realm = GoridemeAplication.getInstance(FoodActivity.this).getRealmInstance();
                        realm.beginTransaction();
                        realm.delete(KategoriRestoran.class);
                        realm.copyToRealm(kategoriRestoran);
                        realm.commitTransaction();

                        realm.beginTransaction();
                        realm.delete(Restoran.class);
                        realm.copyToRealm(restoran);
                        realm.commitTransaction();

//                        kategoriRealmResults = realm.where(KategoriRestoran.class).findAll();
//                        kategoriRealmResults.sort("idKategori", Sort.DESCENDING);
//                        KategoriItem kategoriItem;
//                        for (int i = 0; i < kategoriRealmResults.size(); i++) {
//                            kategoriItem = new KategoriItem(FoodActivity.this);
//                            kategoriItem.idKategori = kategoriRealmResults.get(i).getIdKategori();
//                            kategoriItem.kategori = kategoriRealmResults.get(i).getKategori();
//                            kategoriItem.image = kategoriRealmResults.get(i).getFotoKategori();
//                            kategoriAdapter.add(kategoriItem);
//                            Log.e("ADD CARGO", kategoriRealmResults.get(i).getIdKategori()+"");
//                        }

                        kategoriAdapter.clear();
                        KategoriItem kategoriItem;
                        for (KategoriRestoran kategori : kategoriRestoran) {
                            kategoriItem = new KategoriItem(FoodActivity.this);
                            kategoriItem.idKategori = kategori.getIdKategori();
                            kategoriItem.kategori = kategori.getKategori();
                            kategoriItem.image = kategori.getFotoKategori();
                            kategoriAdapter.add(kategoriItem);
                            Log.e("ADD KATEGORI", kategori.getIdKategori() + "");
                        }

                        List<PromosiMFood> promosiMFoods = dataRestoran.getPromosiMFood();
//                        Toast.makeText(FoodActivity.this, "size = "+promosiMFoods.size(), Toast.LENGTH_SHORT).show();
                        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), promosiMFoods);
                        autoScrollViewPager.setAdapter(pagerAdapter);
                        circleIndicator.setViewPager(autoScrollViewPager);
                        autoScrollViewPager.setInterval(20000);
                        autoScrollViewPager.startAutoScroll(20000);
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

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public List<PromosiMFood> banners = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager, List<PromosiMFood> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideRestoFragment.newInstance(banners.get(position).getId(),
                    banners.get(position).getFoto(),
                    banners.get(position).getIdResto());
//            switch (position) {
//                case 0:
//                    return SlideFragment.newInstance(0, "Page # 1");
//                case 1:
//                    return SlideFragment.newInstance(1, "Page # 2");
//                case 2:
//                    return SlideFragment.newInstance(2, "Page # 3");
//                case 3:
//                    return SlideFragment.newInstance(3, "Page # 4");
//                case 4:
//                    return SlideFragment.newInstance(4, "Page # 5");
//
//
//                default:
//                    return null;
//            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void showRecycler() {
        nearmeRecyler.setVisibility(View.VISIBLE);
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
