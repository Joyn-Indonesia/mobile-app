package com.joyn.me.mService;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.BookService;
import com.joyn.me.model.DataMservice;
import com.joyn.me.model.JenisService;
import com.joyn.me.model.TipeAC;
import com.joyn.me.model.User;
import com.joyn.me.model.json.book.GetDataMserviceResponseJson;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class mServiceActivity extends AppCompatActivity implements Validator.ValidationListener {

    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;

    @NotEmpty
    @BindView(com.joyn.me.R.id.service_form)
    EditText serviceForm;

    @NotEmpty
    @BindView(com.joyn.me.R.id.actype_form)
    EditText actypeForm;

    @NotEmpty
    @BindView(com.joyn.me.R.id.quantity_form)
    EditText quantityForm;

    @NotEmpty
    @BindView(com.joyn.me.R.id.residential_form)
    EditText residentialForm;

    @BindView(com.joyn.me.R.id.problem_form)
    EditText problemForm;

    @BindView(com.joyn.me.R.id.mService_btnnext)
    Button nextButton;

    public static final String FITUR_KEY = "FiturKey";

    private Validator validator;
    private User loginUser;

    private int fiturId;
    private int jenisId;
    private long harga;
    private int acId;
    private double fare;

    private List<JenisService> layanan;
    private List<TipeAC> tipe;
    private String[] itemLayanan;
    private String[] itemTipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_mservice);
        ButterKnife.bind(this);

        loginUser = GoridemeAplication.getInstance(this).getLoginUser();

        Intent intent = getIntent();
        fiturId = intent.getIntExtra(FITUR_KEY, -1);

        validator = new Validator(this);
        validator.setValidationListener(this);

        getDataMservice();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceForm.setFocusable(false);
        serviceForm.setCursorVisible(true);
        serviceForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mServiceActivity.this);
                builder.setTitle("SERVICE");
                if (itemLayanan.length < 1) {
                    builder.setMessage("Connection to server lost, check your internet connection.");
                } else {
                    builder.setItems(itemLayanan, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            JenisService jenis = layanan.get(position);
                            serviceForm.setText(jenis.getJenisService());
                            jenisId = jenis.getId();
                            harga = jenis.getHarga();
                        }
                    });
                }
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        actypeForm.setFocusable(false);
        actypeForm.setCursorVisible(true);
        actypeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mServiceActivity.this);
                builder.setTitle("AC TYPE");
                if (itemTipe.length < 1) {
                    builder.setMessage("Connection to server lost, check your internet connection.");
                } else {
                    builder.setItems(itemTipe, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            TipeAC ac = tipe.get(position);
                            actypeForm.setText(ac.getAcType());
                            acId = ac.getNomor();
                            fare = ac.getFare();
                        }
                    });
                }
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        quantityForm.setFocusable(false);
        quantityForm.setCursorVisible(true);
        quantityForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] itemQuantity = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mServiceActivity.this);
                builder.setTitle("QUANTITY");
                builder.setItems(itemQuantity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        String selectedResidential = Arrays.asList(itemQuantity).get(position);
                        quantityForm.setText(selectedResidential);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        residentialForm.setFocusable(false);
        residentialForm.setCursorVisible(true);
        residentialForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] itemResidential = new String[]{"Rumah", "Toko", "Swalayan", "Kantor"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mServiceActivity.this);
                builder.setTitle("RESIDENTIAL");
                builder.setItems(itemResidential, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        String selectedResidential = Arrays.asList(itemResidential).get(position);
                        residentialForm.setText(selectedResidential);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
    }

    private void getDataMservice() {
        loginUser = GoridemeAplication.getInstance(this).getLoginUser();
        final BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.getDataMservice().enqueue(new Callback<GetDataMserviceResponseJson>() {
            @Override
            public void onResponse(Call<GetDataMserviceResponseJson> call, Response<GetDataMserviceResponseJson> response) {
                DataMservice dataMservice = response.body().getDataMservice();
                layanan = dataMservice.getJenisServiceList();
                tipe = dataMservice.getTipeACList();
                Log.d(mServiceActivity.class.getSimpleName(), "Number of layanan: " + layanan.size());
                Log.d(mServiceActivity.class.getSimpleName(), "Number of tipe ac: " + tipe.size());

                itemLayanan = new String[layanan.size()];
                for (int i = 0; i < layanan.size(); i++) {
                    itemLayanan[i] = layanan.get(i).getJenisService();
                }

                itemTipe = new String[tipe.size()];
                for (int i = 0; i < tipe.size(); i++) {
                    itemTipe[i] = tipe.get(i).getAcType();
                }
            }

            @Override
            public void onFailure(Call<GetDataMserviceResponseJson> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Connection to server lost, check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onNextButtonClick() {
        Intent sendDataOrder = new Intent(mServiceActivity.this, mServiceBook.class);
        sendDataOrder.putExtra(mServiceBook.FITUR_KEY, fiturId);
        sendDataOrder.putExtra(mServiceBook.JENIS_KEY, jenisId);
        sendDataOrder.putExtra(mServiceBook.JENIS_STRING, serviceForm.getText().toString());
        sendDataOrder.putExtra(mServiceBook.HARGA_KEY, harga);
        sendDataOrder.putExtra(mServiceBook.ACTYPE_KEY, acId);
        sendDataOrder.putExtra(mServiceBook.ACTYPE_STRING, actypeForm.getText().toString());
        sendDataOrder.putExtra(mServiceBook.FARE_KEY, fare);
        sendDataOrder.putExtra(mServiceBook.QUANTITY_KEY, Integer.parseInt(quantityForm.getText().toString()));
        sendDataOrder.putExtra(mServiceBook.RESIDENTIAL_KEY, residentialForm.getText().toString());
        if (problemForm == null) {
            sendDataOrder.putExtra(mServiceBook.PROBLEM_KEY, " ");
        } else {
            sendDataOrder.putExtra(mServiceBook.PROBLEM_KEY, problemForm.getText().toString());
        }
        startActivity(sendDataOrder);
    }

    @Override
    public void onValidationSucceeded() {
        onNextButtonClick();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

}