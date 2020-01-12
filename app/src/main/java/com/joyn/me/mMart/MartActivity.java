package com.joyn.me.mMart;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.MapDirectionAPI;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.BookService;
import com.joyn.me.gmap.directions.Route;
import com.joyn.me.model.Driver;
import com.joyn.me.model.Fitur;
import com.joyn.me.model.User;
import com.joyn.me.model.json.book.GetNearRideCarRequestJson;
import com.joyn.me.splash.SplashActivity;

import com.joyn.me.gmap.directions.Directions;
import com.joyn.me.home.submenu.TopUpActivity;
import com.joyn.me.model.Pesanan;
import com.joyn.me.model.json.book.GetNearRideCarResponseJson;
import com.joyn.me.model.json.book.RequestMartRequestJson;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MartActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private static final int REQUEST_PERMISSION_LOCATION = 991;

    public static final int MART_LOCATION = 1;
    public static final int DESTINATION_LOCATION = 2;

    public static final String FITUR_KEY = "FiturKey";

    @BindView(com.joyn.me.R.id.mart_martName)
    EditText martName;
    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;
    @BindView(com.joyn.me.R.id.mart_martLocation)
    LinearLayout martGetLocationButton;
    @BindView(com.joyn.me.R.id.mart_martLocationText)
    TextView martGetLocationText;

    @BindView(com.joyn.me.R.id.mart_plusList)
    TextView productAdd;
    @BindView(com.joyn.me.R.id.mart_minusList)
    TextView productRemove;
    @BindView(com.joyn.me.R.id.mart_menuListRecycler)
    RecyclerView productListRecycler;

    @BindView(com.joyn.me.R.id.mart_destinationButton)
    LinearLayout destinationGetLocationButton;
    @BindView(com.joyn.me.R.id.mart_destinationText)
    TextView destinationGetLocationText;
    @BindView(com.joyn.me.R.id.mart_detailsName)
    EditText destinationDetails;

    @BindView(com.joyn.me.R.id.mart_estimatedCost)
    EditText estimatedCostEdit;

    @BindView(com.joyn.me.R.id.mart_order)
    Button orderButton;

    @BindView(com.joyn.me.R.id.mart_distance)
    TextView distanceText;

    @BindView(com.joyn.me.R.id.mart_price)
    TextView priceText;

    @BindView(com.joyn.me.R.id.mart_topUp)
    Button topUpButton;

    @BindView(com.joyn.me.R.id.mart_detailOrder)
    LinearLayout detailOrder;

    @BindView(com.joyn.me.R.id.mart_paymentGroup)
    RadioGroup paymentGroup;
    @BindView(com.joyn.me.R.id.mart_mPayPayment)
    RadioButton mPayButton;
    @BindView(com.joyn.me.R.id.mart_cashPayment)
    RadioButton cashButton;
    @BindView(com.joyn.me.R.id.mart_mPayBalance)
    TextView mPayBalanceText;
    @BindView(com.joyn.me.R.id.discountText)
    TextView discountText;


    private List<MartItem> martItemList;
    private FastItemAdapter<MartItem> martAdapter;

    private LatLng martLatLng;
    private LatLng destinationLatLng;

    private Fitur fitur;

    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Polyline directionLine;
    private Marker martMarker;
    private Marker destinationMarker;

    private Location lastKnownLocation;

    private List<Driver> driverAvailable;
    private List<Marker> driverMarkers;

    private double jarak;
    private long harga;
    private long saldoMpay;
    private double timeDistance = 0;
//    DiskonMpay diskonMpay;

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void setupGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        updateLastLocation();
    }

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (lastKnownLocation != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15f)
            );

            gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));

            fetchNearDriver();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        User userLogin = GoridemeAplication.getInstance(this).getLoginUser();
        String formattedText = String.format(Locale.US, "$ %s ,-",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));
        mPayBalanceText.setText(formattedText);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_mart);
        ButterKnife.bind(this);

        martItemList = new ArrayList<>();
        initializeRecyclerView();
//        diskonMpay = GoridemeAplication.getInstance(this).getDiskonMpay();

        driverAvailable = new ArrayList<>();
        driverMarkers = new ArrayList<>();

        martGetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMartLocation();
            }
        });

        destinationGetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDestinationLocation();
            }
        });
        User userLogin = new User();
        if (GoridemeAplication.getInstance(this).getLoginUser() != null) {
            userLogin = GoridemeAplication.getInstance(this).getLoginUser();
        } else {
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }

        saldoMpay = userLogin.getmPaySaldo();
        setupGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.joyn.me.R.id.map);
        mapFragment.getMapAsync(this);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderClick();
            }
        });

        Intent intent = getIntent();
        int selectedFitur = intent.getIntExtra(FITUR_KEY, -1);
        Realm realm = Realm.getDefaultInstance();

        if (selectedFitur != -1)
            fitur = realm.where(Fitur.class).equalTo("idFitur", selectedFitur).findFirst();

        discountText.setText("Discount " + fitur.getDiskon() + " with Wallet");
        cashButton.setChecked(true);

        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (paymentGroup.getCheckedRadioButtonId()) {
                    case com.joyn.me.R.id.mart_mPayPayment:
                        long biayaTotal = (long) (harga * fitur.getBiayaAkhir());
                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        String formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                    case com.joyn.me.R.id.mart_cashPayment:
                        biayaTotal = harga;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
                        priceText.setText(formattedText);
                        break;

                }
            }
        });

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TopUpActivity.class));
            }
        });
    }

    private void fetchNearDriver() {
        User loginUser = GoridemeAplication.getInstance(this).getLoginUser();

        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
        param.setLatitude(lastKnownLocation.getLatitude());
        param.setLongitude(lastKnownLocation.getLongitude());

        service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
            @Override
            public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                if (response.isSuccessful()) {
                    driverAvailable = response.body().getData();
                    createMarker();
                }
            }

            @Override
            public void onFailure(Call<GetNearRideCarResponseJson> call, Throwable t) {

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchNearDriver(double latitude, double longitude) {
        if (lastKnownLocation != null) {
            User loginUser = GoridemeAplication.getInstance(this).getLoginUser();

            BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
            GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
            param.setLatitude(latitude);
            param.setLongitude(longitude);


            service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
                @Override
                public void onResponse(Call<GetNearRideCarResponseJson> call, Response<GetNearRideCarResponseJson> response) {
                    if (response.isSuccessful()) {
                        driverAvailable = response.body().getData();
                        createMarker();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<GetNearRideCarResponseJson> call, Throwable t) {

                }
            });
        }
    }

    private void createMarker() {
        if (!driverAvailable.isEmpty()) {
            for (Marker m : driverMarkers) {
                m.remove();
            }
            driverMarkers.clear();

            for (Driver driver : driverAvailable) {
                LatLng currentDriverPos = new LatLng(driver.getLatitude(), driver.getLongitude());
                driverMarkers.add(
                        gMap.addMarker(new MarkerOptions()
                                .position(currentDriverPos)
                                .icon(BitmapDescriptorFactory.fromResource(com.joyn.me.R.drawable.ic_m_ride_pin)))
                );

            }
        }
    }

    private void orderClick() {
        if (martName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill mart name", Toast.LENGTH_SHORT).show();
        }

        if (martLatLng == null && martGetLocationText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select mart location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (destinationLatLng == null && destinationGetLocationText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select destination location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (estimatedCostEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill the estimated cost", Toast.LENGTH_SHORT).show();
            return;
        }

        List<MartItem> items = martAdapter.getAdapterItems();
        for (MartItem item : items) {
            if (item.getNamaProduk().trim().isEmpty()) {
                Toast.makeText(this, "Please fill the product name", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<Pesanan> pesananList = new ArrayList<>();
        for (MartItem item : items) {
            Pesanan pesanan = new Pesanan();
            pesanan.setNamaBarang(item.getNamaProduk());
            pesanan.setQty(item.getQuantity());
            pesananList.add(pesanan);
        }


        User loginUser = GoridemeAplication.getInstance(this).getLoginUser();

        RequestMartRequestJson param = new RequestMartRequestJson();
        param.setIdPelanggan(loginUser.getId());
        param.setOrderFitur(String.valueOf(fitur.getIdFitur()));
        param.setStartLatitude(destinationLatLng.latitude);
        param.setStartLongitude(destinationLatLng.longitude);
        param.setTokoLatitude(martLatLng.latitude);
        param.setTokoLongitude(martLatLng.longitude);
        param.setAlamatAsal(destinationGetLocationText.getText().toString());
        param.setAlamatToko(martGetLocationText.getText().toString());
        param.setNamaToko(martName.getText().toString());
        param.setJarak(jarak);

        param.setHarga(harga);
        param.setCatatan(destinationDetails.getText().toString());
        param.setEstimasiBiaya(Long.valueOf(estimatedCostEdit.getText().toString()));
        param.setPesanan(pesananList);

        if (mPayButton.isChecked()) {
            param.setHarga((long) (harga * fitur.getBiayaAkhir()));
        }
//        switch (paymentGroup.getCheckedRadioButtonId()) {
//            case R.id.mart_mPayPayment:
//                param.setHarga(harga/2);
//
//                break;
//            case R.id.mart_cashPayment:
//
//                break;
//        }

        if (driverAvailable.isEmpty()) {
            AlertDialog dialog = new AlertDialog.Builder(MartActivity.this)
                    .setMessage("Sorry, there are no drivers around.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            dialog.show();
        } else {
            Intent intent = new Intent(MartActivity.this, MartWaitingActivity.class);
            intent.putExtra(MartWaitingActivity.REQUEST_PARAM, param);
            intent.putExtra(MartWaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
            intent.putExtra("time_distance", timeDistance);
            startActivity(intent);
        }

    }

    private void getMartLocation() {
        Intent intent = new Intent(MartActivity.this, LocationPickerActivity.class);
        intent.putExtra(LocationPickerActivity.FORM_VIEW_INDICATOR, MART_LOCATION);
        startActivityForResult(intent, LocationPickerActivity.LOCATION_PICKER_ID);
    }

    private void getDestinationLocation() {
        Intent intent = new Intent(MartActivity.this, LocationPickerActivity.class);
        intent.putExtra(LocationPickerActivity.FORM_VIEW_INDICATOR, DESTINATION_LOCATION);
        startActivityForResult(intent, LocationPickerActivity.LOCATION_PICKER_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationPickerActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                int fillData = data.getIntExtra(LocationPickerActivity.FORM_VIEW_INDICATOR, -1);
                String address = data.getStringExtra(LocationPickerActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(LocationPickerActivity.LOCATION_LATLNG);

                switch (fillData) {
                    case MART_LOCATION:
                        martGetLocationText.setText(address);
                        martLatLng = latLng;

                        if (martMarker != null) martMarker.remove();
                        martMarker = gMap.addMarker(new MarkerOptions()
                                .position(martLatLng)
                                .title("Pick Up")
                                .icon(BitmapDescriptorFactory.fromResource(com.joyn.me.R.drawable.ic_location_orange)));
                        fetchNearDriver(martLatLng.latitude, martLatLng.longitude);

                        break;
                    case DESTINATION_LOCATION:
                        destinationGetLocationText.setText(address);
                        destinationLatLng = latLng;

                        if (destinationMarker != null) destinationMarker.remove();
                        destinationMarker = gMap.addMarker(new MarkerOptions()
                                .position(destinationLatLng)
                                .title("Destination")
                                .icon(BitmapDescriptorFactory.fromResource(com.joyn.me.R.drawable.ic_location_blue2)));

                        break;
                }

                requestRoute();
            }
        }
    }

    private void initializeRecyclerView() {
        if (martItemList.isEmpty()) martItemList.add(new MartItem());

        martAdapter = new FastItemAdapter<>();

        productListRecycler.setLayoutManager(new LinearLayoutManager(this));
        productListRecycler.setAdapter(martAdapter);
        productListRecycler.setNestedScrollingEnabled(false);

        martAdapter.setNewList(martItemList);
        productAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        productRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem();
            }
        });
    }

    private void addItem() {
        if (martItemList.size() + 1 <= 20) martItemList.add(new MartItem());
        martAdapter.setNewList(martItemList);
        martAdapter.notifyDataSetChanged();
    }

    private void removeItem() {
        if (martItemList.size() - 1 > 0) martItemList.remove(martItemList.size() - 1);
        martAdapter.setNewList(martItemList);
        martAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void requestRoute() {
        if (martLatLng != null && destinationLatLng != null) {
            MapDirectionAPI.getDirection(martLatLng, destinationLatLng).enqueue(updateRouteCallback);
        }
    }

    private void updateDistance(long distance) {
        detailOrder.setVisibility(View.VISIBLE);

        float km = ((float) distance) / 1000f;

        this.jarak = km;

        String format = String.format(Locale.US, "Distance (%.1f Km)", km);
        distanceText.setText(format);

        long biayaTotal = fitur.getBiaya();

        if (biayaTotal % 1 != 0)
            biayaTotal = (1 - (biayaTotal % 1)) + biayaTotal;

        this.harga = biayaTotal;

        if (mPayButton.isChecked()) {
            biayaTotal = (long) (biayaTotal * fitur.getBiayaAkhir());
        }

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
        priceText.setText(formattedText);

        if (saldoMpay < (harga * fitur.getBiayaAkhir())) {
            mPayButton.setEnabled(false);
            cashButton.toggle();
        } else {
            mPayButton.setEnabled(true);
        }
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(MartActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(MartActivity.this, com.joyn.me.R.color.directionLine))
                        .width(5));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(MartActivity.this, json);
                final long time = MapDirectionAPI.getTimeDistance(MartActivity.this, json);
                if (distance >= 0) {
                    MartActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);
                            updateDistance(distance);
                            timeDistance = time / 60;

                        }
                    });
                }
            }
        }
    };


}
