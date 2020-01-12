package com.joyn.me.mRideCar;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.FCMHelper;
import com.joyn.me.api.MapDirectionAPI;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.UserService;
import com.joyn.me.config.General;
import com.joyn.me.gmap.directions.Route;
import com.joyn.me.home.ChatActivity;
import com.joyn.me.home.MainActivity;
import com.joyn.me.model.Driver;
import com.joyn.me.model.FCMType;
import com.joyn.me.model.ResponseCode;
import com.joyn.me.model.Transaksi;
import com.joyn.me.model.User;
import com.joyn.me.model.json.fcm.CancelBookRequestJson;
import com.joyn.me.model.json.fcm.CancelBookResponseJson;
import com.joyn.me.model.json.fcm.DriverRequest;
import com.joyn.me.model.json.fcm.DriverResponse;
import com.joyn.me.service.GoridemeMessagingService;
import com.joyn.me.utils.Log;
import com.joyn.me.utils.db.DBHandler;
import com.joyn.me.utils.db.Queries;

import com.joyn.me.gmap.directions.Directions;
import com.joyn.me.model.json.fcm.FCMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class InProgressActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(com.joyn.me.R.id.rideCar_title)
    TextView title;
    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;
    @BindView(com.joyn.me.R.id.rideCar_pickUpText)
    TextView pickUpText;
    @BindView(com.joyn.me.R.id.rideCar_destinationText)
    TextView destinationText;
    @BindView(com.joyn.me.R.id.rideCar_distance)
    TextView distanceText;
    @BindView(com.joyn.me.R.id.rideCar_price)
    TextView priceText;
    @BindView(com.joyn.me.R.id.driver_image)
    RoundedImageView driverImage;
    @BindView(com.joyn.me.R.id.driver_name)
    TextView driverName;
    @BindView(com.joyn.me.R.id.order_number)
    TextView orderNumber;
    @BindView(com.joyn.me.R.id.driver_police_number)
    TextView driverPoliceNumber;
    @BindView(com.joyn.me.R.id.driver_car)
    TextView driverCar;
    @BindView(com.joyn.me.R.id.driver_arriving_time)
    TextView driverArrivingTime;
    @BindView(com.joyn.me.R.id.chat_driver)
    LinearLayout chatDriver;
    @BindView(com.joyn.me.R.id.call_driver)
    LinearLayout callDriver;
    @BindView(com.joyn.me.R.id.cancelBook)
    TextView cancelBook;
    @BindView(com.joyn.me.R.id.price)
    RelativeLayout price;


    private GoogleMap gMap;
    private boolean isMapReady = false;
    private Location lastKnownLocation;
    private static final int REQUEST_PERMISSION_LOCATION = 991;
    private static final int REQUEST_PERMISSION_CALL = 992;
    private GoogleApiClient googleApiClient;
    private LatLng pickUpLatLng;
    private LatLng destinationLatLng;
    private Polyline directionLine;
    private Marker pickUpMarker;
    private Marker destinationMarker;
    private Context context;
    Bundle orderBundle;
    private boolean isCancelable = true;
    Driver driver;
    DriverRequest request;
    User loginUser;
    Realm realm;

    private okhttp3.Callback updateRouteCallback = new okhttp3.Callback() {
        @Override
        public void onFailure(okhttp3.Call call, IOException e) {

        }

        @Override
        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            if (response.isSuccessful()) {
                final String json = response.body().string();
                final long distance = MapDirectionAPI.getDistance(InProgressActivity.this, json);
                if (distance >= 0) {
                    InProgressActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateLineDestination(json);
//                            updateDistance(distance);
                        }
                    });
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_in_progress);
        ButterKnife.bind(this);

        context = getApplicationContext();
        realm = Realm.getDefaultInstance();


//        readTransaction();

        loginUser = GoridemeAplication.getInstance(InProgressActivity.this).getLoginUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.joyn.me.R.id.rideCar_mapView);
        mapFragment.getMapAsync(this);

        driver = (Driver) getIntent().getSerializableExtra("driver");
        request = (DriverRequest) getIntent().getSerializableExtra("request");
        Log.e("DATA DRIVER", driver.getNamaDepan() + " " + driver.getNamaBelakang());
        Log.e("DATA REQUEST", request.getAlamatAsal() + " to " + request.getAlamatTujuan());


        pickUpText.setText(request.getAlamatAsal());
        destinationText.setText(request.getAlamatTujuan());
        String format = String.format(Locale.US, "Distance (%.1f Km)", request.getJarak());
        distanceText.setText(format);

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(request.getHarga());
        String formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
        priceText.setText(formattedText);


//        distanceText.setText("Jarak (" + request.getJarak() + ") Km");
//        priceText.setText("Rp " + request.getHarga());
        Glide.with(getApplicationContext()).load(driver.getFoto()).into(driverImage);
        driverName.setText(driver.getNamaDepan() + " " + driver.getNamaBelakang());
        orderNumber.setText("Order no. " + request.getIdTransaksi());
        driverPoliceNumber.setText(driver.getNomor_kendaraan());
        driverCar.setText(driver.getMerek() + " " + driver.getTipe() + " (" + driver.getWarna() + ")");
        driverArrivingTime.setText("Estimate " + getIntent().getDoubleExtra("time_distance", 0) + " menit");

        pickUpLatLng = new LatLng(request.getStartLatitude(), request.getStartLongitude());
        destinationLatLng = new LatLng(request.getEndLatitude(), request.getEndLongitude());

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Chat with driver", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("reg_id", driver.getRegId());
                startActivity(intent);
            }
        });

        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InProgressActivity.this);
                alertDialogBuilder.setTitle("Call Driver");
                alertDialogBuilder.setMessage("You want to call driver? " + driver.getNoTelepon() + "?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (ActivityCompat.checkSelfPermission(InProgressActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(InProgressActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
                                    return;
                                }

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + driver.getNoTelepon()));
                                startActivity(callIntent);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCancelable) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InProgressActivity.this);
                    alertDialogBuilder.setTitle("Cancel Order");
                    alertDialogBuilder.setMessage("Are you sure?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    cancelOrder();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot be canceled, the trip has already begun!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void cancelOrder() {
        User loginUser = GoridemeAplication.getInstance(InProgressActivity.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();
        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();
        request.id_driver = driver.getId();

        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("order canceled")) {
                        Toast.makeText(InProgressActivity.this, "Order Canceled!", Toast.LENGTH_SHORT).show();
                        new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                        finish();
                    } else {
                        Toast.makeText(InProgressActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(InProgressActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = FCMType.ORDER;
        response.setIdTransaksi(this.request.getIdTransaksi());
        response.setResponse(DriverResponse.REJECT);

        FCMMessage message = new FCMMessage();
        message.setTo(driver.getRegId());
        message.setData(response);


        FCMHelper.sendMessage(General.FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e("CANCEL REQUEST", "sent");
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                Log.e("CANCEL REQUEST", "failed");
            }
        });
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        isMapReady = true;

        updateLastLocation(true);
        requestRoute();

        if (pickUpMarker != null) pickUpMarker.remove();
        pickUpMarker = gMap.addMarker(new MarkerOptions()
                .position(pickUpLatLng)
                .title("Pick Up")
                .icon(BitmapDescriptorFactory.fromResource(com.joyn.me.R.drawable.ic_location_orange)));


        if (destinationMarker != null) destinationMarker.remove();
        destinationMarker = gMap.addMarker(new MarkerOptions()
                .position(destinationLatLng)
                .title("Destination")
                .icon(BitmapDescriptorFactory.fromResource(com.joyn.me.R.drawable.ic_location_blue2)));

    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(true);

        if (pickUpLatLng != null) {
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        pickUpLatLng, 15f)
                );

                gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            }
//            fetchNearDriver();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation(true);
            } else {
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }

        if (requestCode == REQUEST_PERMISSION_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Call permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Call permission restricted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestRoute() {
        if (pickUpLatLng != null && destinationLatLng != null) {
            MapDirectionAPI.getDirection(pickUpLatLng, destinationLatLng).enqueue(updateRouteCallback);
        }
    }

    private void updateLineDestination(String json) {
        Directions directions = new Directions(InProgressActivity.this);
        try {
            List<Route> routes = directions.parse(json);

            if (directionLine != null) directionLine.remove();
            if (routes.size() > 0) {
                directionLine = gMap.addPolyline((new PolylineOptions())
                        .addAll(routes.get(0).getOverviewPolyLine())
                        .color(ContextCompat.getColor(InProgressActivity.this, com.joyn.me.R.color.directionLine))
                        .width(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GoridemeMessagingService.BROADCAST_ORDER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            orderBundle = intent.getExtras();
            orderHandler(orderBundle.getInt("code"));
        }
    };

    private void orderHandler(int code) {
        switch (code) {
            case ResponseCode.REJECT:
                Log.e("DRIVER RESPONSE", "reject");
                isCancelable = false;
                break;
            case ResponseCode.ACCEPT:
                Log.e("DRIVER RESPONSE", "accept");
                break;
            case ResponseCode.CANCEL:
                Log.e("DRIVER RESPONSE", "cancel");
                finish();
                break;

            case ResponseCode.START:
                Log.e("DRIVER RESPONSE", "start");
                isCancelable = false;
                Toast.makeText(getApplicationContext(), "Perjalanan Anda sudah Dimulai!", Toast.LENGTH_SHORT).show();
                break;
            case ResponseCode.FINISH:
                Log.e("DRIVER RESPONSE", "finish");
                isCancelable = false;
//                new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                Toast.makeText(getApplicationContext(), "Anda sudah sampai di tempat tujuan.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RateDriverActivity.class);
                intent.putExtra("id_transaksi", request.getIdTransaksi());
                intent.putExtra("id_pelanggan", loginUser.getId());
                intent.putExtra("driver_photo", driver.getFoto());
                intent.putExtra("id_driver", driver.getId());
                startActivity(intent);
                finish();
                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onMessageEvent(final DriverResponse response) {
        Log.e("IN PROGRESS", response.getResponse() + " " + response.getId() + " " + response.getIdTransaksi());

    }

    private void readTransaction() {
        RealmResults<Transaksi> results = realm.where(Transaksi.class).findAll();

        Log.e("ALL TRANSACTION", results.toString());
        Log.e("TRANSACTION SIZE", results.size() + "");
        for (int i = 0; i < results.size(); i++) {
            Log.e("TRANSACTION ID", results.get(i).getId() + "");
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void detail(View view) {
    }
}
