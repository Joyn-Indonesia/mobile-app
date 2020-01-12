package com.joyn.me.home.submenu.help;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.joyn.me.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HelpFragment extends Fragment {

    @BindView(com.joyn.me.R.id.help_recyclerView)
    RecyclerView recyclerView;

    private FastItemAdapter<HelpItem> adapter;

    private List<HelpItem> helpItemList;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.joyn.me.R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        context = getContext();

        helpItemList = new ArrayList<>();
        helpItemList.add(new HelpItem(com.joyn.me.R.mipmap.pro_mobil, com.joyn.me.R.string.home_mCar));
        helpItemList.add(new HelpItem(com.joyn.me.R.mipmap.pro_motor, com.joyn.me.R.string.home_mRide));
        helpItemList.add(new HelpItem(com.joyn.me.R.mipmap.pro_paket, com.joyn.me.R.string.home_mSend));
        helpItemList.add(new HelpItem(com.joyn.me.R.mipmap.pro_box, com.joyn.me.R.string.home_mBox));
        helpItemList.add(new HelpItem(com.joyn.me.R.mipmap.pro_pijat, com.joyn.me.R.string.home_mMassage));
        helpItemList.add(new HelpItem(com.joyn.me.R.mipmap.pro_food, com.joyn.me.R.string.home_mFood));
        helpItemList.add(new HelpItem(com.joyn.me.R.mipmap.pro_jasa, com.joyn.me.R.string.home_mService));


        adapter = new FastItemAdapter<>();
        adapter.add(helpItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent helpIntent = new Intent(context, HelpActivity.class);
                helpIntent.putExtra("id", position);
                startActivity(helpIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));


    }
}
