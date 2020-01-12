package com.joyn.me.home.submenu;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;

import com.joyn.me.GoridemeAplication;
import com.joyn.me.utils.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.UserService;
import com.joyn.me.model.User;
import com.joyn.me.model.json.user.TopupRequestJson;
import com.joyn.me.model.json.user.TopupResponseJson;
import com.joyn.me.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TopUpActivity extends AppCompatActivity {

    private static final String TAG = TopUpActivity.class.getSimpleName();

    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;
    String bukti;
    private String bankName = "";
    TopUpActivity activity;
//    private Uri file;

    @BindView(com.joyn.me.R.id.daftarbank)
    Button daftarbank;
    @BindView(com.joyn.me.R.id.pemilikRekening)
    EditText name;
    @BindView(com.joyn.me.R.id.nomorRekening)
    EditText accountNumber;
    @BindView(com.joyn.me.R.id.nominalTransfer)
    EditText nominal;
    @BindView(com.joyn.me.R.id.spinBank)
    Spinner spinner;
    @BindView(com.joyn.me.R.id.butUploadBukti)
    Button upload;
    @BindView(com.joyn.me.R.id.butTopup)
    TextView topup;
    @BindView(com.joyn.me.R.id.other_bank_layout)
    TextInputLayout otherBankLayout;
    @BindView(com.joyn.me.R.id.other_bank)
    EditText otherBank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_top_up);
        ButterKnife.bind(this);
        activity = TopUpActivity.this;
        final User userLogin = GoridemeAplication.getInstance(this).getLoginUser();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            upload.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

//                Toast.makeText(activity, "Index : "+i, Toast.LENGTH_SHORT).show();
                if (i != 3) {
                    bankName = spinner.getSelectedItem().toString();
                    otherBankLayout.setVisibility(GONE);
                } else {
                    otherBankLayout.setVisibility(VISIBLE);
                    otherBank.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        otherBank.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bankName = otherBank.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nominal.addTextChangedListener(Utility.currencyTW(nominal));

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
//                take_photo();
            }
        });

        topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTopUp();

//                JSONObject jVer = new JSONObject();
//                try {
//                    jVer.put("id", userLogin.getId());
//                    jVer.put("no_rekening", accountNumber.getText().toString());
//                    jVer.put("jumlah", nominal.getText().toString());
//                    jVer.put("atas_nama", name.getText().toString());
//                    jVer.put("bank", bankName);
//                    jVer.put("bukti", bukti);
//
//                    verifikasiTopup(jVer);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        });

    }


    private void submitTopUp() {
        final ProgressDialog pd = showLoading();

        User user = GoridemeAplication.getInstance(this).getLoginUser();
        TopupRequestJson request = new TopupRequestJson();
        request.id = user.getId();
        request.atas_nama = name.getText().toString();
        request.no_rekening = accountNumber.getText().toString();
        request.jumlah = getNominal();
        request.bank = bankName;
        request.bukti = bukti;


        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.topUp(request).enqueue(new Callback<TopupResponseJson>() {
            @Override
            public void onResponse(Call<TopupResponseJson> call, Response<TopupResponseJson> response) {
                if (response.isSuccessful()) {
                    pd.dismiss();

                    if (response.body().message.equals("success")) {
                        Toast.makeText(activity, "Terima kasih. Verifikasi akan segera diproses..", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(activity, "Verifikasi bermasalah..", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<TopupResponseJson> call, Throwable t) {
                t.printStackTrace();
                pd.dismiss();
                Toast.makeText(TopUpActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getNominal() {
        String originalString = nominal.getText().toString();

        Long longval;
        if (originalString.contains(".")) {
            originalString = originalString.replaceAll("[$.]", "");
        }
        if (originalString.contains(",")) {
            originalString = originalString.replaceAll(",", "");
        }
        if (originalString.contains("$ ")) {
            originalString = originalString.replaceAll("$ ", "");
        }
        if (originalString.contains("$")) {
            originalString = originalString.replaceAll("$", "");
        }
        if (originalString.contains("R")) {
            originalString = originalString.replaceAll("R", "");
        }
        if (originalString.contains("p")) {
            originalString = originalString.replaceAll("p", "");
        }
        if (originalString.contains(" ")) {
            originalString = originalString.replaceAll(" ", "");
        }

        return originalString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                upload.setEnabled(true);
            }
        }
    }


    public void takePhoto() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Invoice_" + timeStamp;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), imageFileName);


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);
        } else {
            File file = new File(photo.getPath());
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileProvider", file);
            imageUri = photoUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {

                    activity.getContentResolver().notifyChange(imageUri, null);
                    ContentResolver cr = activity.getContentResolver();
                    Bitmap bitmap;
                    try {
//                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                                android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
//                        Cursor cursor = cr.query(imageUri,
//                                filePathColumn, null, null, null);
//                        cursor.moveToFirst();
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        String imgDecodableString = cursor.getString(columnIndex);
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
//                        bitmap = decodeFile(imgDecodableString, 200);
                        Log.d("after_comppres", String.valueOf(bitmap.getByteCount()));
                        bukti = compressJSON(bitmap);
                        if (!bukti.equals("")) {
                            TextView centang = (TextView) activity.findViewById(com.joyn.me.R.id.centang);
                            centang.setVisibility(VISIBLE);
                            Button butUploadBukti = (Button) activity.findViewById(com.joyn.me.R.id.butUploadBukti);
                            butUploadBukti.setVisibility(GONE);

                        }

                    } catch (Exception e) {
                        Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }
                }
                break;
            default:
                break;
        }
    }

//    private void verifikasiTopup(JSONObject jVer){
//
//        final ProgressDialog pd= showLoading();
//        Log.d("JVER", jVer.toString());
//
//        HTTPHelper.getInstance(activity).verifikasiTopUp(jVer, new NetworkActionResult() {
//            @Override
//            public void onSuccess(JSONObject obj) {
//                try {
//                    pd.dismiss();
//                    if(obj.getString("message").equals("success")){
//                        Toast.makeText(activity, "Terima kasih. Verifikasi akan segera diproses..", Toast.LENGTH_SHORT).show();
//                    }else{
//                        Toast.makeText(activity, "Verifikasi bermasalah..", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(String message) {
//
//            }
//
//            @Override
//            public void onError(String message) {
//                pd.dismiss();
//                showWarning();
//
//            }
//        });
//
//    }

//    private MaterialDialog showWarning() {
//        final MaterialDialog md = new MaterialDialog.Builder(activity)
//                .title("Connection problem.")
//                .content("Please try again.")
//                .icon(new IconicsDrawable(activity)
//                        .icon(FontAwesome.Icon.faw_exclamation_triangle)
//                        .color(Color.YELLOW)
//                        .sizeDp(24))
//                .positiveText("Close")
//                .positiveColor(Color.DKGRAY)
//                .show();
//
//        View positive = md.getActionButton(DialogAction.POSITIVE);
//
//        positive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                md.dismiss();
//            }
//        });
//        return md;
//    }

    private ProgressDialog showLoading() {
        ProgressDialog ad = ProgressDialog.show(activity, "", "Loading...", true);
        return ad;
    }

    public String compressJSON(Bitmap bmp) {
        byte[] imageBytes0;
        ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos0);
        imageBytes0 = baos0.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes0, Base64.DEFAULT);
        return encodedImage;
    }

    public void pindah(View view) {
        Intent intent = new Intent(TopUpActivity.this, DaftarbankActivity.class);
        startActivity(intent);
    }
}