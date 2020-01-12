package com.joyn.me.home.submenu.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.BookService;
import com.joyn.me.mBox.BoxActivity;
import com.joyn.me.mBox.BoxOrder;
import com.joyn.me.mFood.RestoranItemHome;
import com.joyn.me.mMassage.MassageActivity;
import com.joyn.me.mRideCar.RideCarActivity;
import com.joyn.me.mSend.SendActivity;
import com.joyn.me.model.DataRestoran;
import com.joyn.me.model.PesananFood;
import com.joyn.me.model.User;
import com.joyn.me.model.json.book.GetKendaraanAngkutResponseJson;
import com.joyn.me.model.json.user.GetBannerResponseJson;
import com.joyn.me.model.json.user.GetSaldoResponseJson;
import com.joyn.me.splash.SplashActivity;
import com.joyn.me.utils.SnackbarController;

import com.joyn.me.api.service.UserService;
import com.joyn.me.home.submenu.TopUpActivity;
import com.joyn.me.mFood.FoodActivity;
import com.joyn.me.mFood.FoodMenuActivity;
import com.joyn.me.mFood.NearmeActivity;
import com.joyn.me.mMart.MartActivity;
import com.joyn.me.mService.mServiceActivity;
import com.joyn.me.model.Banner;
import com.joyn.me.model.Fitur;
import com.joyn.me.model.KategoriRestoran;
import com.joyn.me.model.KendaraanAngkut;
import com.joyn.me.model.Restoran;
import com.joyn.me.model.json.book.GetDataRestoRequestJson;
import com.joyn.me.model.json.book.GetDataRestoResponseJson;
import com.joyn.me.model.json.user.GetSaldoRequestJson;
import com.joyn.me.utils.ConnectivityUtils;
import com.joyn.me.utils.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import io.realm.Realm;
import io.realm.RealmResults;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(com.joyn.me.R.id.home_mCar)
    RelativeLayout buttonMangCar;

    @BindView(com.joyn.me.R.id.home_mRide)
    RelativeLayout buttonMangRide;

    @BindView(com.joyn.me.R.id.home_mSend)
    RelativeLayout buttonMangSend;

    @BindView(com.joyn.me.R.id.home_mBox)
    RelativeLayout buttonMangBox;

    @BindView(com.joyn.me.R.id.home_mMart)
    RelativeLayout buttonMangMart;

    @BindView(com.joyn.me.R.id.home_mMassage)
    RelativeLayout buttonMangMassage;

    @BindView(com.joyn.me.R.id.home_mFood)
    RelativeLayout buttonMangFood;

    @BindView(com.joyn.me.R.id.home_mService)
    RelativeLayout butonMangService;

    @BindView(com.joyn.me.R.id.home_mPayBalance)
    TextView mPayBalance;

    @BindView(com.joyn.me.R.id.home_topUpButton)
    Button topUpButton;

    @BindView(com.joyn.me.R.id.slide_viewPager)
    AutoScrollViewPager slideViewPager;

    @BindView(com.joyn.me.R.id.slide_viewPager_indicator)
    CircleIndicator slideIndicator;

    @BindView(com.joyn.me.R.id.nearme_recycler)
    RecyclerView nearmeRecycler;

    @BindView(com.joyn.me.R.id.nearme_progress)
    ProgressBar progress;

    @BindView(com.joyn.me.R.id.show_more)
    TextView showMore;

    @BindView(com.joyn.me.R.id.cargo_type_recyclerView)
    RecyclerView cargoTypeRecyclerView;

    @BindView(com.joyn.me.R.id.show_more_box)
    TextView showMoreBox;

    private SnackbarController snackbarController;

    private boolean connectionAvailable;
    private boolean isDataLoaded = false;

    public static final String FITUR_KEY = "FiturKey";
    public static final String CARGO = "cargo";

    int featureId;
    FastItemAdapter<CargoItemHome> itemAdapter;

    private Realm realm;
    private RealmResults<Restoran> restoranRealmResults;
    private FastItemAdapter<RestoranItemHome> restoranAdapter;
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    private boolean requestUpdate = true;
    private List<KategoriRestoran> kategoriRestoran;
    private List<Restoran> restoran;

    private int successfulCall;

    public ArrayList<Banner> banners = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SnackbarController) {
            snackbarController = (SnackbarController) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.joyn.me.R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        Intent intent = getActivity().getIntent();
        featureId = intent.getIntExtra(FITUR_KEY, -1);

        realm = Realm.getDefaultInstance();
        LoadKendaraan();
        itemAdapter = new FastItemAdapter<>();
        cargoTypeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        cargoTypeRecyclerView.setAdapter(itemAdapter);
        itemAdapter.withItemEvent(new ClickEventHook<CargoItemHome>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CargoItemHome.ViewHolder) {
                    return ((CargoItemHome.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<CargoItemHome> fastAdapter, CargoItemHome item) {
                KendaraanAngkut selectedCargo = realm.where(KendaraanAngkut.class).equalTo("idKendaraan", itemAdapter.getAdapterItem(position).id).findFirst();
                Log.e("BUTTON", "CLICKED, ID : " + selectedCargo.getIdKendaraan());
                Intent intent = new Intent(getActivity(), BoxOrder.class);
                intent.putExtra(BoxOrder.FITUR_KEY, featureId);
                intent.putExtra(BoxOrder.KENDARAAN_KEY, selectedCargo.getIdKendaraan());
                startActivity(intent);
            }
        });


        showRecycler();
        realm = Realm.getDefaultInstance();
        restoranAdapter = new FastItemAdapter<>();
        nearmeRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        nearmeRecycler.setAdapter(restoranAdapter);
        restoranAdapter.withOnClickListener(new FastAdapter.OnClickListener<RestoranItemHome>() {
            @Override
            public boolean onClick(View v, IAdapter<RestoranItemHome> adapter, RestoranItemHome item, int position) {
                Log.e("BUTTON", "CLICKED");
                Restoran selectedResto = realm.where(Restoran.class).
                        equalTo("id", restoranAdapter.getAdapterItem(position).id).findFirst();
                Intent intent = new Intent(getActivity(), FoodMenuActivity.class);
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
            restoranItemHome = new RestoranItemHome(getActivity());
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


        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NearmeActivity.class);
                startActivity(intent);
            }
        });
        showMoreBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BoxActivity.class);
                startActivity(intent);
            }
        });
        buttonMangRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangRideClick();
            }
        });
        buttonMangCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangCarClick();
            }
        });
        buttonMangSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMangSendClick();
            }
        });
        buttonMangBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMangBoxClick();
            }
        });
        connectionAvailable = false;
        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTopUpClick();
            }
        });
        buttonMangMart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangMartClick();
            }
        });
        butonMangService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMServiceClick();
            }
        });
        buttonMangMassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMMassageClick();
            }
        });
        buttonMangFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMangFoodClick();
            }
        });


        realm = GoridemeAplication.getInstance(getActivity()).getRealmInstance();
        getImageBanner();


    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();

    }


    @Override
    public void onStop() {
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

    private void getImageBanner() {
        User loginUser = new User();
        if (GoridemeAplication.getInstance(getActivity()).getLoginUser() != null) {
            loginUser = GoridemeAplication.getInstance(getActivity()).getLoginUser();
        } else {
            startActivity(new Intent(getActivity(), SplashActivity.class));
            getActivity().finish();
        }

        UserService userService = ServiceGenerator.createService(UserService.class,
                loginUser.getEmail(), loginUser.getPassword());
        userService.getBanner().enqueue(new Callback<GetBannerResponseJson>() {
            @Override
            public void onResponse(Call<GetBannerResponseJson> call, Response<GetBannerResponseJson> response) {
                if (response.isSuccessful()) {
                    banners = response.body().data;
                    Log.e("Image", response.body().data.get(0).foto);
                    MyPagerAdapter pagerAdapter = new MyPagerAdapter(getFragmentManager(), banners);
                    slideViewPager.setAdapter(pagerAdapter);
                    slideIndicator.setViewPager(slideViewPager);
                    slideViewPager.setInterval(20000);
                    slideViewPager.startAutoScroll(20000);
                }

            }

            @Override
            public void onFailure(Call<GetBannerResponseJson> call, Throwable t) {
            }
        });
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;
        public ArrayList<Banner> banners = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fragmentManager, ArrayList<Banner> banners) {
            super(fragmentManager);
            this.banners = banners;
        }

        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SlideFragment.newInstance(banners.get(position).id, banners.get(position).foto);
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


    @Override
    public void onResume() {
        super.onResume();
        final Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PesananFood.class);
        realm.commitTransaction();
        successfulCall = 0;
        connectionAvailable = ConnectivityUtils.isConnected(getActivity());
        if (!connectionAvailable) {
            if (snackbarController != null) snackbarController.showSnackbar(
                    com.joyn.me.R.string.text_noInternet, Snackbar.LENGTH_INDEFINITE, com.joyn.me.R.string.text_close,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            return;
                        }
                    });
        } else {
            updateMPayBalance();
        }
    }


    private void showRecycler() {
        nearmeRecycler.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void onMangSendClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 5).findFirst();
        Intent intent = new Intent(getActivity(), SendActivity.class);
        intent.putExtra(SendActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onTopUpClick() {
        Intent intent = new Intent(getActivity(), TopUpActivity.class);
        startActivity(intent);
    }

    private void onMangRideClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 1).findFirst();
        Intent intent = new Intent(getActivity(), RideCarActivity.class);
        intent.putExtra(RideCarActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangCarClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 2).findFirst();
        Intent intent = new Intent(getActivity(), RideCarActivity.class);
        intent.putExtra(RideCarActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangMartClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 4).findFirst();
        Intent intent = new Intent(getActivity(), MartActivity.class);
        intent.putExtra(MartActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangBoxClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 7).findFirst();
        Intent intent = new Intent(getActivity(), BoxActivity.class);
        intent.putExtra(BoxActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMServiceClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 8).findFirst();
        Intent intent = new Intent(getActivity(), mServiceActivity.class);
        intent.putExtra(mServiceActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMMassageClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 6).findFirst();
        Intent intent = new Intent(getActivity(), MassageActivity.class);
        intent.putExtra(mServiceActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void onMangFoodClick() {
        Fitur selectedFitur = realm.where(Fitur.class).equalTo("idFitur", 3).findFirst();
        Intent intent = new Intent(getActivity(), FoodActivity.class);
        intent.putExtra(FoodActivity.FITUR_KEY, selectedFitur.getIdFitur());
        getActivity().startActivity(intent);
    }

    private void LoadKendaraan() {
        User loginUser = GoridemeAplication.getInstance(getActivity()).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.getKendaraanAngkut().enqueue(new Callback<GetKendaraanAngkutResponseJson>() {
            @Override
            public void onResponse(Call<GetKendaraanAngkutResponseJson> call, Response<GetKendaraanAngkutResponseJson> response) {
                if (response.isSuccessful()) {

                    Realm realm = GoridemeAplication.getInstance(getActivity()).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(KendaraanAngkut.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();


                    CargoItemHome cargoItemHome;
                    for (KendaraanAngkut cargo : response.body().getData()) {
                        cargoItemHome = new CargoItemHome(getActivity());
                        cargoItemHome.featureId = featureId;
                        cargoItemHome.id = cargo.getIdKendaraan();
                        cargoItemHome.type = cargo.getKendaraanAngkut();
                        cargoItemHome.price = cargo.getHarga();
                        cargoItemHome.image = cargo.getFotoKendaraan();
                        cargoItemHome.dimension = cargo.getDimensiKendaraan();
                        cargoItemHome.maxWeight = cargo.getMaxweightKendaraan();
                        itemAdapter.add(cargoItemHome);
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

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (lastKnownLocation != null) {
            if (requestUpdate) {
                getDataRestoran();


            }
        }
    }


    private void getDataRestoran() {
        if (lastKnownLocation != null) {
            User loginUser = GoridemeAplication.getInstance(getActivity()).getLoginUser();
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
                        Realm realm = GoridemeAplication.getInstance(getActivity()).getRealmInstance();
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


    private void updateMPayBalance() {
        User loginUser = GoridemeAplication.getInstance(getActivity()).getLoginUser();
        UserService userService = ServiceGenerator.createService(
                UserService.class, loginUser.getEmail(), loginUser.getPassword());

        GetSaldoRequestJson param = new GetSaldoRequestJson();
        param.setId(loginUser.getId());
        userService.getSaldo(param).enqueue(new Callback<GetSaldoResponseJson>() {
            @Override
            public void onResponse(Call<GetSaldoResponseJson> call, Response<GetSaldoResponseJson> response) {
                if (response.isSuccessful()) {
                    String formattedText = String.format(Locale.US, "$ %s ,-",
                            NumberFormat.getNumberInstance(Locale.US).format(response.body().getData()));
                    mPayBalance.setText(formattedText);
                    successfulCall++;

                    if (HomeFragment.this.getActivity() != null) {
                        Realm realm = GoridemeAplication.getInstance(HomeFragment.this.getActivity()).getRealmInstance();
                        User loginUser = GoridemeAplication.getInstance(HomeFragment.this.getActivity()).getLoginUser();
                        realm.beginTransaction();
                        loginUser.setmPaySaldo(response.body().getData());
                        realm.commitTransaction();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetSaldoResponseJson> call, Throwable t) {

            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        requestUpdate = true;
    }


}
