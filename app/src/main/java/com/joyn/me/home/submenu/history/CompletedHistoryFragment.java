package com.joyn.me.home.submenu.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joyn.me.GoridemeAplication;
import com.joyn.me.adapter.HistoryAdapter;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.UserService;
import com.joyn.me.model.User;
import com.joyn.me.model.json.menu.HistoryRequestJson;
import com.joyn.me.utils.Log;
import com.joyn.me.model.ItemHistory;
import com.joyn.me.model.json.menu.HistoryResponseJson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CompletedHistoryFragment extends Fragment implements HistoryFragment.OnSwipeRefresh {

    @BindView(com.joyn.me.R.id.completed_recyclerView)
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;

    public CompletedHistoryFragment() {
    }

    public static CompletedHistoryFragment newInstance() {
        CompletedHistoryFragment fragment = new CompletedHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(com.joyn.me.R.layout.fragment_completed_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        requestData();

    }

    private void requestData() {
        User user = GoridemeAplication.getInstance(getActivity()).getLoginUser();
        HistoryRequestJson request = new HistoryRequestJson();
        request.id = user.getId();

        UserService service = ServiceGenerator.createService(UserService.class, user.getEmail(), user.getPassword());
        service.getCompleteHistory(request).enqueue(new Callback<HistoryResponseJson>() {
            @Override
            public void onResponse(Call<HistoryResponseJson> call, Response<HistoryResponseJson> response) {
                if (response.isSuccessful()) {
                    ArrayList<ItemHistory> data = response.body().data;

//                    Log.e("HISTORY", data.get(0).toString());

                    for (int i = 0; i < data.size(); i++) {
                        switch (data.get(i).order_fitur) {
                            case "m-ride":
                                data.get(i).image_id = com.joyn.me.R.mipmap.pro_motor;
                                break;
                            case "m-car":
                                data.get(i).image_id = com.joyn.me.R.mipmap.pro_mobil;
                                break;
                            case "m-send":
                                data.get(i).image_id = com.joyn.me.R.mipmap.pro_paket;
                                break;
                            case "m-mart":
                                data.get(i).image_id = com.joyn.me.R.mipmap.pro_toko;
                                break;
                            case "m-box":
                                data.get(i).image_id = com.joyn.me.R.mipmap.pro_box;
                                break;
                            case "m-service":
                                data.get(i).image_id = com.joyn.me.R.mipmap.pro_jasa;
                                break;
                            case "m-massage":
                                data.get(i).image_id = com.joyn.me.R.mipmap.pro_pijat;
                                break;
                            case "m-food":
                                data.get(i).image_id = com.joyn.me.R.mipmap.pro_food;
                                break;
                        }
                    }

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

                    recyclerView.setLayoutManager(layoutManager);
                    historyAdapter = new HistoryAdapter(getContext(), data, true);
                    recyclerView.setAdapter(historyAdapter);
                    if (response.body().data.size() == 0) {
                        Log.d("HISTORY", "Empty");
                    }
                }
            }

            @Override
            public void onFailure(Call<HistoryResponseJson> call, Throwable t) {
                t.printStackTrace();
//                Toast.makeText(getActivity(), "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("System error:", t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    public void onRefresh() {
        requestData();
    }
}
