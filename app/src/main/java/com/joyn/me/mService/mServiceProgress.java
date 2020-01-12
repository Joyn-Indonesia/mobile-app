package com.joyn.me.mService;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.FCMHelper;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.home.ChatActivity;
import com.joyn.me.home.MainActivity;
import com.joyn.me.mRideCar.RateDriverActivity;
import com.joyn.me.model.Driver;
import com.joyn.me.model.FCMType;
import com.joyn.me.model.ResponseCode;
import com.joyn.me.model.User;
import com.joyn.me.model.json.fcm.CancelBookRequestJson;
import com.joyn.me.model.json.fcm.CancelBookResponseJson;
import com.joyn.me.utils.db.DBHandler;
import com.joyn.me.utils.db.Queries;

import com.joyn.me.api.service.UserService;
import com.joyn.me.model.ItemHistory;
import com.joyn.me.model.json.fcm.DriverRequest;
import com.joyn.me.model.json.fcm.DriverResponse;
import com.joyn.me.model.json.fcm.FCMMessage;
import com.joyn.me.utils.Log;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.joyn.me.config.General.FCM_KEY;
import static com.joyn.me.service.GoridemeMessagingService.BROADCAST_ORDER;

public class mServiceProgress extends AppCompatActivity {

    @BindView(com.joyn.me.R.id.btn_home)
    ImageView btnHome;

    @BindView(com.joyn.me.R.id.text_ordernum)
    TextView textOrdernum;

    @BindView(com.joyn.me.R.id.driver_image)
    RoundedImageView driverImage;

    @BindView(com.joyn.me.R.id.driver_name)
    TextView driverName;

    @BindView(com.joyn.me.R.id.driver_number)
    TextView driverNumber;

    @BindView(com.joyn.me.R.id.btn_chat)
    LinearLayout btnChat;

    @BindView(com.joyn.me.R.id.btn_call)
    LinearLayout btnCall;

    @BindView(com.joyn.me.R.id.value_service)
    TextView textService;

    @BindView(com.joyn.me.R.id.value_actype)
    TextView textActype;

    @BindView(com.joyn.me.R.id.value_quantity)
    TextView textQuantity;

    @BindView(com.joyn.me.R.id.value_problem)
    TextView textProblem;

    @BindView(com.joyn.me.R.id.value_location)
    TextView textLocation;

    @BindView(com.joyn.me.R.id.value_price)
    TextView textPrice;

    @BindView(com.joyn.me.R.id.btn_cancel)
    Button btnCancel;

    private static final int REQUEST_PERMISSION_CALL = 992;

    private Context context;
    Bundle orderBundle;
    private boolean isCancelable = true;
    Driver driver;
    DriverRequest request;
    User loginUser;
    Realm realm;
    ItemHistory transaction;
    private boolean isCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_mservice_progress);
        ButterKnife.bind(this);

        context = getApplicationContext();
        realm = Realm.getDefaultInstance();
        loginUser = GoridemeAplication.getInstance(mServiceProgress.this).getLoginUser();

        driver = (Driver) getIntent().getSerializableExtra("driver");
        request = (DriverRequest) getIntent().getSerializableExtra("request");

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(mServiceProgress.this, MainActivity.class);
                startActivity(home);
            }
        });

        transaction = (ItemHistory) getIntent().getSerializableExtra("transaction");
        isCompleted = getIntent().getBooleanExtra("isCompleted", false);

        textOrdernum.setText("Order no. " + request.getIdTransaksi());
        Glide.with(getApplicationContext()).load(driver.getFoto()).into(driverImage);
        driverName.setText(driver.getNamaDepan() + " " + driver.getNamaBelakang());
        driverNumber.setText(driver.getNoTelepon());

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("reg_id", driver.getRegId());
                startActivity(intent);
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mServiceProgress.this);
                alertDialogBuilder.setTitle("Call driver");
                alertDialogBuilder.setMessage("Do you want to call " + driver.getNoTelepon() + "?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (ActivityCompat.checkSelfPermission(mServiceProgress.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(mServiceProgress.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PERMISSION_CALL);
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

        textService.setText(request.getJenisService());
        textActype.setText(request.getAcType());
        textQuantity.setText("" + request.getQuantity());
        textProblem.setText(request.getProblem());
        textLocation.setText(request.getAlamatAsal());

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(request.getHarga());
        String formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
        textPrice.setText(formattedText);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCancelable) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mServiceProgress.this);
                    alertDialogBuilder.setTitle("Batalkan Pesanan");
                    alertDialogBuilder.setMessage("Are you sure you want to cancel this order?");
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
                    Toast.makeText(getApplicationContext(), "You can't cancel order, trip already started!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (isCompleted) {
//            chatDriver.setVisibility(View.INVISIBLE);
//            callDriver.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);
            btnChat.setVisibility(View.GONE);
            btnCall.setVisibility(View.INVISIBLE);
        }
    }

    private void cancelOrder() {
        User loginUser = GoridemeAplication.getInstance(mServiceProgress.this).getLoginUser();
        CancelBookRequestJson request = new CancelBookRequestJson();
        request.id = loginUser.getId();
        request.id_transaksi = this.request.getIdTransaksi();

        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        service.cancelOrder(request).enqueue(new Callback<CancelBookResponseJson>() {
            @Override
            public void onResponse(Call<CancelBookResponseJson> call, Response<CancelBookResponseJson> response) {
                if (response.isSuccessful()) {
                    if (response.body().mesage.equals("Order Canceled")) {
                        Toast.makeText(mServiceProgress.this, "Order Canceled!", Toast.LENGTH_SHORT).show();
                        new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                        finish();
                    } else {
                        Toast.makeText(mServiceProgress.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelBookResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mServiceProgress.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        DriverResponse response = new DriverResponse();
        response.type = FCMType.ORDER;
        response.setIdTransaksi(this.request.getIdTransaksi());
        response.setResponse(DriverResponse.REJECT);

        FCMMessage message = new FCMMessage();
        message.setTo(driver.getRegId());
        message.setData(response);


        FCMHelper.sendMessage(FCM_KEY, message).enqueue(new okhttp3.Callback() {
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
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_ORDER));
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
                Toast.makeText(getApplicationContext(), "Your trip is started", Toast.LENGTH_SHORT).show();
                break;
            case ResponseCode.FINISH:
                Log.e("DRIVER RESPONSE", "finish");
                isCancelable = false;
//                new Queries(new DBHandler(getApplicationContext())).truncate(DBHandler.TABLE_CHAT);
                Toast.makeText(getApplicationContext(), "Your trip is finished", Toast.LENGTH_SHORT).show();
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
}
