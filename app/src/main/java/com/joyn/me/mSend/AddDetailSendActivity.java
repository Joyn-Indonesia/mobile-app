package com.joyn.me.mSend;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.model.Driver;
import com.joyn.me.model.User;
import com.joyn.me.model.json.book.RequestSendRequestJson;

import com.joyn.me.home.submenu.TopUpActivity;
import com.joyn.me.model.Fitur;
import com.joyn.me.utils.Log;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class AddDetailSendActivity extends AppCompatActivity {
    @BindView(com.joyn.me.R.id.mSend_distance)
    TextView distanceText;
    @BindView(com.joyn.me.R.id.mSend_price)
    TextView priceText;
    @BindView(com.joyn.me.R.id.mSend_paymentGroup)
    RadioGroup paymentGroup;
    @BindView(com.joyn.me.R.id.mSend_mPayPayment)
    RadioButton mPayButton;
    @BindView(com.joyn.me.R.id.mSend_cashPayment)
    RadioButton cashButton;
    @BindView(com.joyn.me.R.id.mSend_topUp)
    Button topUpButton;
    @BindView(com.joyn.me.R.id.mSend_mPayBalance)
    TextView mPayBalanceText;
    @BindView(com.joyn.me.R.id.mSend_order)
    Button orderButton;
    @BindView(com.joyn.me.R.id.mSend_goods_description)
    EditText goodsDescription;
    @BindView(com.joyn.me.R.id.mSend_sender_name)
    EditText senderName;
    @BindView(com.joyn.me.R.id.mSend_sender_phone)
    EditText senderPhone;
    @BindView(com.joyn.me.R.id.mSend_receiver_name)
    EditText receiverName;
    @BindView(com.joyn.me.R.id.mSend_receiver_phone)
    EditText receiverPhone;
    @BindView(com.joyn.me.R.id.discountText)
    TextView discountText;
    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;

    private double distance;
    private long price;
    private LatLng pickUpLatLang;
    private LatLng destinationLatLang;
    private String pickup;
    private String destination;
    private ArrayList<Driver> driverAvailable;
    private double timeDistance = 0;
    //    DiskonMpay diskonMpay;
    private long mpayBalance;
    private Fitur fitur;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_send_add_detail);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        User userLogin = GoridemeAplication.getInstance(this).getLoginUser();
        mpayBalance = userLogin.getmPaySaldo();
//        diskonMpay = GoridemeAplication.getInstance(this).getDiskonMpay();
        Intent intent = getIntent();
        if (intent.hasExtra("distance")) {
            distance = intent.getDoubleExtra("distance", 0);
            price = intent.getLongExtra("price", 0);
            pickUpLatLang = intent.getParcelableExtra("pickup_latlng");
            destinationLatLang = intent.getParcelableExtra("destination_latlng");
            pickup = intent.getStringExtra("pickup");
            destination = intent.getStringExtra("destination");
            timeDistance = intent.getDoubleExtra("time_distance", 0);
            driverAvailable = (ArrayList<Driver>) intent.getSerializableExtra("driver");
            int selectedFitur = intent.getIntExtra(SendActivity.FITUR_KEY, -1);

            if (selectedFitur != -1)
                fitur = realm.where(Fitur.class).equalTo("idFitur", selectedFitur).findFirst();

            discountText.setText("Discount " + fitur.getDiskon() + " with Wallet");
        }

        long biayaTotal = price;
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
        priceText.setText(formattedText);

        float km = ((float) distance);
        String format = String.format(Locale.US, "Distance (%.1f Km)", km);
        distanceText.setText(format);
        if (mpayBalance < (price * fitur.getBiayaAkhir())) {
            mPayButton.setEnabled(false);
            cashButton.toggle();
        } else {
            mPayButton.setEnabled(true);
        }

        cashButton.setChecked(true);
        paymentGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (paymentGroup.getCheckedRadioButtonId()) {
                    case com.joyn.me.R.id.mSend_mPayPayment:
                        long biayaTotal = (long) (price * fitur.getBiayaAkhir());
                        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        String formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                    case com.joyn.me.R.id.mSend_cashPayment:
                        biayaTotal = price;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                    default:
                        biayaTotal = price;
                        formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
                        formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
                        priceText.setText(formattedText);
                        break;
                }
            }
        });


        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOrderButtonClick();
            }
        });

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TopUpActivity.class));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void onOrderButtonClick() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case com.joyn.me.R.id.mSend_mPayPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(AddDetailSendActivity.this)
                            .setMessage("Sorry, there are no drivers around you.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestSendRequestJson param = new RequestSendRequestJson();
                    User userLogin = GoridemeAplication.getInstance(this).getLoginUser();
                    param.idPelanggan = userLogin.getId();
                    param.orderFitur = "5";
                    param.startLatitude = pickUpLatLang.latitude;
                    param.startLongitude = pickUpLatLang.longitude;
                    param.endLatitude = destinationLatLang.latitude;
                    param.endLongitude = destinationLatLang.longitude;
                    param.jarak = this.distance;
                    param.harga = (long) (this.price * fitur.getBiayaAkhir());
                    param.alamatAsal = pickup;
                    param.alamatTujuan = destination;
                    param.namaPengirim = senderName.getText().toString();
                    param.teleponPengirim = senderPhone.getText().toString();
                    param.namaPenerima = receiverName.getText().toString();
                    param.teleponPenerima = receiverPhone.getText().toString();
                    param.namaBarang = goodsDescription.getText().toString();


                    Log.e("M-PAY", "used");
                    param.pakaiMpay = 1;

                    Intent intent = new Intent(AddDetailSendActivity.this, SendWaitingActivity.class);
                    intent.putExtra(SendWaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(SendWaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }


                break;
            case com.joyn.me.R.id.mSend_cashPayment:
                if (driverAvailable.isEmpty()) {
                    AlertDialog dialog = new AlertDialog.Builder(AddDetailSendActivity.this)
                            .setMessage("Sorry, there are no drivers around you.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    RequestSendRequestJson param = new RequestSendRequestJson();
                    User userLogin = GoridemeAplication.getInstance(this).getLoginUser();
                    param.idPelanggan = userLogin.getId();
                    param.orderFitur = "5";
                    param.startLatitude = pickUpLatLang.latitude;
                    param.startLongitude = pickUpLatLang.longitude;
                    param.endLatitude = destinationLatLang.latitude;
                    param.endLongitude = destinationLatLang.longitude;
                    param.jarak = this.distance;
                    param.harga = this.price;
                    param.alamatAsal = pickup;
                    param.alamatTujuan = destination;
                    param.namaPengirim = senderName.getText().toString();
                    param.teleponPengirim = senderPhone.getText().toString();
                    param.namaPenerima = receiverName.getText().toString();
                    param.teleponPenerima = receiverPhone.getText().toString();
                    param.namaBarang = goodsDescription.getText().toString();

                    Log.e("M-PAY", "not using m pay");
                    param.pakaiMpay = 0;


                    Intent intent = new Intent(AddDetailSendActivity.this, SendWaitingActivity.class);
                    intent.putExtra(SendWaitingActivity.REQUEST_PARAM, param);
                    intent.putExtra(SendWaitingActivity.DRIVER_LIST, (ArrayList) driverAvailable);
                    intent.putExtra("time_distance", timeDistance);
                    startActivity(intent);
                }
                break;
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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
