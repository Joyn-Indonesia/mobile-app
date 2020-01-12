package com.joyn.me.home.submenu.history.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.BookService;
import com.joyn.me.model.DetailTransaksiMSend;
import com.joyn.me.model.User;
import com.joyn.me.model.json.book.detailTransaksi.GetDataTransaksiMSendResponse;
import com.joyn.me.model.json.book.detailTransaksi.GetDataTransaksiRequest;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by David Studio on 24/02/2017.
 */

public class MSendDetailActivity extends AppCompatActivity {

    public static final String ID_TRANSAKSI = "IDTransaksi";

    @BindView(com.joyn.me.R.id.mSendDetail_title)
    TextView title;

    @BindView(com.joyn.me.R.id.mSendDetail_tanggal)
    TextView tanggalPengiriman;
    @BindView(com.joyn.me.R.id.mSendDetail_alamatAsal)
    TextView alamatAsal;
    @BindView(com.joyn.me.R.id.mSendDetail_alamatTujuan)
    TextView alamatTujuan;
    @BindView(com.joyn.me.R.id.mSendDetail_namaPenerima)
    TextView namaPenerima;
    @BindView(com.joyn.me.R.id.mSendDetail_nomorPenerima)
    TextView nomorPenerima;
    @BindView(com.joyn.me.R.id.mSendDetail_namaBarang)
    TextView namaBarang;
    @BindView(com.joyn.me.R.id.total_cost)
    TextView totalCost;

    private String idTransaksi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_msend_detail);
        ButterKnife.bind(this);

        title.setText("Detail m-Send");
        idTransaksi = getIntent().getStringExtra(ID_TRANSAKSI);
        Realm realm = GoridemeAplication.getInstance(this).getRealmInstance();
        User loginUser = realm.copyFromRealm(GoridemeAplication.getInstance(this).getLoginUser());
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());

        GetDataTransaksiRequest param = new GetDataTransaksiRequest();
        param.setIdTransaksi(idTransaksi);
        service.getDataTransaksiMSend(param).enqueue(new Callback<GetDataTransaksiMSendResponse>() {
            @Override
            public void onResponse(Call<GetDataTransaksiMSendResponse> call, Response<GetDataTransaksiMSendResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getDataTransaksi().isEmpty()) {
                        onFailure(call, new Exception());
                    } else {
                        DetailTransaksiMSend detail = response.body().getDataTransaksi().get(0);
                        updateUI(detail);
                    }
                } else {
                    onFailure(call, new Exception());
                }
            }

            @Override
            public void onFailure(Call<GetDataTransaksiMSendResponse> call, Throwable t) {
                Toast.makeText(MSendDetailActivity.this, "Silahkan coba lagi lain waktu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(DetailTransaksiMSend detail) {
        SimpleDateFormat sdfTo = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.US);

        String formattedDate = sdfTo.format(detail.getWaktuOrder());
        tanggalPengiriman.setText(formattedDate);
        alamatAsal.setText(detail.getAlamatAsal());
        alamatTujuan.setText(detail.getAlamatTujuan());
        namaPenerima.setText(detail.getNamaPenerima());
        nomorPenerima.setText(detail.getTeleponPenerima());
        namaBarang.setText(detail.getNamaBarang());

        String totalcostt = String.format(Locale.US, "$ %s",
                NumberFormat.getNumberInstance(Locale.US).format(detail.getHarga()));
        totalCost.setText(totalcostt);
    }
}
