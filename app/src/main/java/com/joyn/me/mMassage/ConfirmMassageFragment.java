package com.joyn.me.mMassage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.service.BookService;
import com.joyn.me.model.json.book.massage.RequestMassageRequestJson;

import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.home.submenu.TopUpActivity;
import com.joyn.me.model.DriverMassage;
import com.joyn.me.model.Fitur;
import com.joyn.me.model.User;
import com.joyn.me.model.json.book.massage.RequestMassageResponseJson;
import com.joyn.me.utils.Utils;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ConfirmMassageFragment extends Fragment {

    public static final int LOCATION = 1;

    @BindView(com.joyn.me.R.id.confirmMassage_locationButton)
    LinearLayout locationButton;
    @BindView(com.joyn.me.R.id.confirmMassage_locationText)
    TextView locationText;

    @BindView(com.joyn.me.R.id.confirmMassage_dateTimeButton)
    LinearLayout dateTimeButton;
    @BindView(com.joyn.me.R.id.confirmMassage_dateTimeText)
    TextView dateTimeText;

    @BindView(com.joyn.me.R.id.confirmMassage_yourGenderText)
    TextView yourGenderText;
    @BindView(com.joyn.me.R.id.confirmMassage_massageTypeText)
    TextView massageTypeText;
    @BindView(com.joyn.me.R.id.confirmMassage_durationText)
    TextView durationText;
    @BindView(com.joyn.me.R.id.confirmMassage_therapistPrefsText)
    TextView therapistText;

    @BindView(com.joyn.me.R.id.confirmMassage_additionalNoteEditText)
    EditText additionalNoteEdit;

    @BindView(com.joyn.me.R.id.confirmMassage_orderDetail)
    LinearLayout orderDetail;
    @BindView(com.joyn.me.R.id.confirmMassage_price)
    TextView priceText;

    @BindView(com.joyn.me.R.id.confirmMassage_paymentGroup)
    RadioGroup paymentGroup;

    @BindView(com.joyn.me.R.id.confirmMassage_mPayBalance)
    TextView mPayBalance;

    @BindView(com.joyn.me.R.id.confirmMassage_topUp)
    Button topUpButton;

    @BindView(com.joyn.me.R.id.confirmMassage_order)
    Button orderButton;

    @BindView(com.joyn.me.R.id.confirmMassage_mPayPayment)
    RadioButton mPayButton;

    @BindView(com.joyn.me.R.id.confirmMassage_cashPayment)
    RadioButton cashButton;

    @BindView(com.joyn.me.R.id.discountText)
    TextView discountText;


    private MassagePreference massagePreference;
    private MenuMassageItem massageItem;
    private LatLng latLngLocation;

    private MassageActivity activity;

    private Calendar calendar = Calendar.getInstance();

    private TimePickerDialog timePicker;

    private TimePickerDialog.OnTimeSetListener timeListener;

    private User loginUser;
    private long biayaTotal;

    private ProgressDialog dialog;
    private Fitur fitur;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MassageActivity) activity = (MassageActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.joyn.me.R.layout.fragment_confirm_massage, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        Realm realm = GoridemeAplication.getInstance(activity).getRealmInstance();
        loginUser = realm.copyFromRealm(GoridemeAplication.getInstance(activity).getLoginUser());

        massagePreference = activity.getMassagePreference();
        massageItem = activity.getMassageItem();
        mPayBalance.setText("$ " + loginUser.getmPaySaldo());


        yourGenderText.setText(massagePreference.getGender());
        massageTypeText.setText(Utils.toTitleCase(massageItem.getLayanan()));
        durationText.setText(massagePreference.getDurasiText());
        therapistText.setText(massagePreference.getTherapist());

        formatDateTime();

        timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                formatDateTime();
            }
        };


        fitur = realm.where(Fitur.class).equalTo("idFitur", 6).findFirst();
        discountText.setText("Discount " + fitur.getDiskon() + " with Wallet");

        timePicker = new TimePickerDialog(activity, timeListener, calendar
                .get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        dateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTime();
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                switch (paymentGroup.getCheckedRadioButtonId()) {
//                    case R.id.confirmMassage_cashPayment:
//
//                    case R.id.confirmMassage_mPayPayment:
//                        order();
//                        break;
//                    default:
//                        break;
//                }
                onOrderButtonClick();
            }
        });

        biayaTotal = (long) ((double) (massageItem.getHarga()) * massagePreference.getDurasi());

        if (biayaTotal % 1000 != 0)
            biayaTotal = (1000 - (biayaTotal % 1000)) + biayaTotal;

        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(biayaTotal);
        String formattedText = String.format(Locale.US, "$ %s ,-", formattedTotal);
        priceText.setText(formattedText);

        if (loginUser.getmPaySaldo() < (long) (biayaTotal * fitur.getBiayaAkhir())) {
            mPayButton.setEnabled(false);
            cashButton.toggle();
        } else {
            mPayButton.setEnabled(true);
        }

        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity.getApplicationContext(), TopUpActivity.class));
            }
        });
    }

    private void onOrderButtonClick() {
        switch (paymentGroup.getCheckedRadioButtonId()) {
            case com.joyn.me.R.id.confirmMassage_cashPayment:
                orderMPay();
                break;
            case com.joyn.me.R.id.confirmMassage_mPayPayment:
                order();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        User userLogin = GoridemeAplication.getInstance(activity).getLoginUser();
        String formattedText = String.format(Locale.US, "$ %s ,-",
                NumberFormat.getNumberInstance(Locale.US).format(userLogin.getmPaySaldo()));
        mPayBalance.setText(formattedText);

        if (userLogin.getmPaySaldo() < (biayaTotal * fitur.getBiayaAkhir())) {
            mPayButton.setEnabled(false);
            cashButton.toggle();
        } else {
            mPayButton.setEnabled(true);
        }
    }


    private void orderMPay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        long durasi = 0;
        if (massagePreference.getDurasiText().equalsIgnoreCase("60 minutes")) {
            durasi = 60;
        } else if (massagePreference.getDurasiText().equalsIgnoreCase("90 minutes")) {
            durasi = 90;
        } else if (massagePreference.getDurasiText().equalsIgnoreCase("120 minutes")) {
            durasi = 120;
        }

        RequestMassageRequestJson param = new RequestMassageRequestJson();
        param.setIdPelanggan(loginUser.getId());
        param.setOrderFitur("6");
        param.setAlamatAsal(locationText.getText().toString());
        param.setHarga(biayaTotal);
        param.setStartLatitude(latLngLocation.latitude);
        param.setStartLongitude(latLngLocation.longitude);
        param.setPelangganGender(String.valueOf(massagePreference.getIdGender()));
        param.setPreferGender(String.valueOf(massagePreference.getIdTherapist()));
        param.setKota("1");
        param.setTanggalPelayanan(dateFormat.format(calendar.getTime()));
        param.setLamaPelayanan(durasi);
        param.setMassageMenu(massageItem.getId());
        param.setJamPelayanan(timeFormat.format(calendar.getTime()));
        param.setCatatanTambahan(additionalNoteEdit.getText().toString());
        param.setPakaiMPay(paymentGroup.getCheckedRadioButtonId() == com.joyn.me.R.id.confirmMassage_mPayPayment);

        if (param.isPakaiMPay()) {
            param.setHarga((long) (param.getHarga() * fitur.getBiayaAkhir()));
        }

        dialog = ProgressDialog.show(getActivity(), "Please Wait", "Please wait while your request we send ...", true, false);

        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.requestTransaksiMMassage(param).enqueue(new Callback<RequestMassageResponseJson>() {
            @Override
            public void onResponse(Call<RequestMassageResponseJson> call, Response<RequestMassageResponseJson> response) {
                if (dialog != null) dialog.dismiss();

                if (response.isSuccessful()) {
                    List<DriverMassage> availableDriver = response.body().getListDriver();
                    if (!availableDriver.isEmpty()) {
                        Intent intent = new Intent(activity, WaitingMassageActivity.class);
                        intent.putExtra(WaitingMassageActivity.MASSAGE_RESPONSE, response.body().getData().get(0));
                        intent.putExtra(WaitingMassageActivity.DRIVER_LIST, (Serializable) response.body().getListDriver());
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        onFailure(call, new DriverNotAvailable());
                    }
                } else {
                    onFailure(call, new RequestFailed());
                }
            }

            @Override
            public void onFailure(Call<RequestMassageResponseJson> call, Throwable t) {
                if (dialog != null) dialog.dismiss();

                if (t instanceof DriverNotAvailable) {
                    Toast.makeText(activity, "Driver not found", Toast.LENGTH_SHORT).show();
                } else if (t instanceof RequestFailed) {
                    Toast.makeText(activity, "Request failed. Please try to request again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Request failed. Please try requesting again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void order() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        long durasi = 0;
        if (massagePreference.getDurasiText().equalsIgnoreCase("60 minutes")) {
            durasi = 60;
        } else if (massagePreference.getDurasiText().equalsIgnoreCase("90 minutes")) {
            durasi = 90;
        } else if (massagePreference.getDurasiText().equalsIgnoreCase("120 minutes")) {
            durasi = 120;
        }

        RequestMassageRequestJson param = new RequestMassageRequestJson();
        param.setIdPelanggan(loginUser.getId());
        param.setOrderFitur("6");
        param.setAlamatAsal(locationText.getText().toString());
        param.setHarga(biayaTotal);
        param.setStartLatitude(latLngLocation.latitude);
        param.setStartLongitude(latLngLocation.longitude);
        param.setPelangganGender(String.valueOf(massagePreference.getIdGender()));
        param.setPreferGender(String.valueOf(massagePreference.getIdTherapist()));
        param.setKota("1");
        param.setTanggalPelayanan(dateFormat.format(calendar.getTime()));
        param.setLamaPelayanan(durasi);
        param.setMassageMenu(massageItem.getId());
        param.setJamPelayanan(timeFormat.format(calendar.getTime()));
        param.setCatatanTambahan(additionalNoteEdit.getText().toString());
        param.setPakaiMPay(paymentGroup.getCheckedRadioButtonId() == com.joyn.me.R.id.confirmMassage_mPayPayment);

        if (param.isPakaiMPay()) {
            param.setHarga((long) (biayaTotal * fitur.getBiayaAkhir()));
        }

        dialog = ProgressDialog.show(getActivity(), "Please Wait", "Please wait while your request we send ...", true, false);

        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.requestTransaksiMMassage(param).enqueue(new Callback<RequestMassageResponseJson>() {
            @Override
            public void onResponse(Call<RequestMassageResponseJson> call, Response<RequestMassageResponseJson> response) {
                if (dialog != null) dialog.dismiss();

                if (response.isSuccessful()) {
                    List<DriverMassage> availableDriver = response.body().getListDriver();
                    if (!availableDriver.isEmpty()) {
                        Intent intent = new Intent(activity, WaitingMassageActivity.class);
                        intent.putExtra(WaitingMassageActivity.MASSAGE_RESPONSE, response.body().getData().get(0));
                        intent.putExtra(WaitingMassageActivity.DRIVER_LIST, (Serializable) response.body().getListDriver());
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        onFailure(call, new DriverNotAvailable());
                    }
                } else {
                    onFailure(call, new RequestFailed());
                }
            }

            @Override
            public void onFailure(Call<RequestMassageResponseJson> call, Throwable t) {
                if (dialog != null) dialog.dismiss();

                if (t instanceof DriverNotAvailable) {
                    Toast.makeText(activity, "There are no drivers available.", Toast.LENGTH_SHORT).show();
                } else if (t instanceof RequestFailed) {
                    Toast.makeText(activity, "Request failed. Please try to request again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Request failed. Please try to request again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getTime() {
        timePicker.show();
    }

    private void formatDateTime() {
        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() - (1 * 60 * 1000)) {
            Toast.makeText(activity, com.joyn.me.R.string.massage_tanggalPesanan, Toast.LENGTH_SHORT).show();
            calendar = Calendar.getInstance();
        }

        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        dateTimeText.setText(sdf.format(calendar.getTime()));
    }

    private void getLocation() {
        Intent intent = new Intent(getActivity(), LocationPickerActivity.class);
        intent.putExtra(LocationPickerActivity.FORM_VIEW_INDICATOR, LOCATION);
        startActivityForResult(intent, LocationPickerActivity.LOCATION_PICKER_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationPickerActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                int fillData = data.getIntExtra(LocationPickerActivity.FORM_VIEW_INDICATOR, -1);
                String address = data.getStringExtra(LocationPickerActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(LocationPickerActivity.LOCATION_LATLNG);

                switch (fillData) {
                    case LOCATION:
                        locationText.setText(address);
                        latLngLocation = latLng;
                        checkDetailStatus();
                        break;
                }

            }
        }
    }

    private void checkDetailStatus() {
        if (latLngLocation != null) {
            orderDetail.setVisibility(View.VISIBLE);
        }
    }

    private static class DriverNotAvailable extends Throwable {
    }

    private static class RequestFailed extends Throwable {
    }
}
