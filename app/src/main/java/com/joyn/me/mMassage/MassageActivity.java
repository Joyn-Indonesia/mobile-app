package com.joyn.me.mMassage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David Studio on 12/20/2017.
 */

public class MassageActivity extends AppCompatActivity {

    @BindView(com.joyn.me.R.id.massage_container)
    FrameLayout container;
    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;

    private MenuMassageItem massageItem;
    private MassagePreference massagePreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_massage);
        ButterKnife.bind(this);

        MenuMassageFragment menuMassage = new MenuMassageFragment();
        getSupportFragmentManager().beginTransaction().add(com.joyn.me.R.id.massage_container, menuMassage).commit();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void addFragmentBackstack(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(com.joyn.me.R.id.massage_container, fragment).addToBackStack(null).commit();
    }

    public MenuMassageItem getMassageItem() {
        return massageItem;
    }

    public void setMassageItem(MenuMassageItem massageItem) {
        this.massageItem = massageItem;
    }

    public MassagePreference getMassagePreference() {
        return massagePreference;
    }

    public void setMassagePreference(MassagePreference massagePreference) {
        this.massagePreference = massagePreference;
    }
}
