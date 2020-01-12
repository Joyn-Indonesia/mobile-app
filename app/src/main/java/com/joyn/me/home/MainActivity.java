package com.joyn.me.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.model.json.user.GetFiturResponseJson;

import com.joyn.me.R;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.UserService;
import com.joyn.me.home.submenu.help.HelpFragment;
import com.joyn.me.home.submenu.history.HistoryFragment;
import com.joyn.me.home.submenu.home.HomeFragment;
import com.joyn.me.home.submenu.setting.SettingFragment;
import com.joyn.me.mMassage.MassagePreference;
import com.joyn.me.mMassage.MenuMassageItem;
import com.joyn.me.model.DiskonMpay;
import com.joyn.me.model.Fitur;
import com.joyn.me.model.MfoodMitra;
import com.joyn.me.model.User;
import com.joyn.me.utils.GoridemeTabProvider;
import com.joyn.me.utils.MenuSelector;
import com.joyn.me.utils.SnackbarController;
import com.joyn.me.utils.view.CustomViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class MainActivity extends AppCompatActivity implements SnackbarController {

    @BindView(R.id.main_container)
    LinearLayout mainLayout;

    @BindView(R.id.main_tabLayout)
    SmartTabLayout mainTabLayout;

    @BindView(R.id.main_viewPager)
    CustomViewPager mainViewPager;

    private Snackbar snackBar;

    private MenuSelector selector;
    private SmartTabLayout.TabProvider tabProvider;
    private MenuMassageItem massageItem;
    private MassagePreference massagePreference;

    private FragmentPagerItemAdapter adapter;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupTabLayoutViewPager();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press the 'BACK' button again to exit ...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void setupTabLayoutViewPager() {
        tabProvider = new GoridemeTabProvider(this);
        selector = (MenuSelector) tabProvider;
        mainTabLayout.setCustomTabView(tabProvider);

        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.main_menuHome, HomeFragment.class)
                .add(R.string.main_menuHistory, HistoryFragment.class)
                .add(R.string.main_menuHelp, HelpFragment.class)
                .add(R.string.main_menuSetting, SettingFragment.class)
                .create());
        mainViewPager.setAdapter(adapter);
        mainTabLayout.setViewPager(mainViewPager);
        mainViewPager.setPagingEnabled(false);

        mainTabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                selector.selectMenu(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFiturGorideme();
    }

    @Override
    public void showSnackbar(@StringRes int stringRes, int duration, @StringRes int actionResText, View.OnClickListener onClickListener) {
        snackBar = Snackbar.make(mainLayout, stringRes, duration);
        if (actionResText != -1 && onClickListener != null) {
            snackBar.setAction(actionResText, onClickListener)
                    .setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        snackBar.show();
    }


    private void updateFiturGorideme() {
        User loginUser = GoridemeAplication.getInstance(this).getLoginUser();
        UserService userService = ServiceGenerator.createService(UserService.class,
                loginUser.getEmail(), loginUser.getPassword());
        userService.getFitur().enqueue(new Callback<GetFiturResponseJson>() {
            @Override
            public void onResponse(Call<GetFiturResponseJson> call, Response<GetFiturResponseJson> response) {
                if (response.isSuccessful()) {
                    Realm realm = GoridemeAplication.getInstance(MainActivity.this).getRealmInstance();
                    realm.beginTransaction();
                    realm.delete(Fitur.class);
                    realm.copyToRealm(response.body().getData());
                    realm.commitTransaction();

                    DiskonMpay diskonMpay = response.body().getDiskonMpay();
                    realm.beginTransaction();
                    realm.delete(DiskonMpay.class);
                    realm.copyToRealm(response.body().getDiskonMpay());
                    realm.commitTransaction();
                    GoridemeAplication.getInstance(MainActivity.this).setDiskonMpay(diskonMpay);

                    MfoodMitra mfoodMitra = response.body().getMfoodMitra();
                    realm.beginTransaction();
                    realm.delete(MfoodMitra.class);
                    realm.copyToRealm(response.body().getMfoodMitra());
                    realm.commitTransaction();
                    GoridemeAplication.getInstance(MainActivity.this).setMfoodMitra(mfoodMitra);
                }
            }

            @Override
            public void onFailure(Call<GetFiturResponseJson> call, Throwable t) {

            }
        });
    }
}
