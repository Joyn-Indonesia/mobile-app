package com.joyn.me.home.submenu.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joyn.me.GoridemeAplication;
import com.joyn.me.model.User;
import com.joyn.me.splash.SplashActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by David Studio on 10/30/2017.
 */

public class SettingFragment extends Fragment {

    @BindView(com.joyn.me.R.id.setting_name)
    TextView settingProfileName;

    @BindView(com.joyn.me.R.id.setting_email)
    TextView settingProfileEmail;

    @BindView(com.joyn.me.R.id.setting_phone)
    TextView settingProfilePhone;

    @BindView(com.joyn.me.R.id.setting_editProfile)
    Button settingEditProfile;

    @BindView(com.joyn.me.R.id.setting_changePassword)
    LinearLayout settingChangePassword;

    @BindView(com.joyn.me.R.id.setting_termOfService)
    LinearLayout settingTermOfService;

    @BindView(com.joyn.me.R.id.setting_privacyPolicy)
    LinearLayout settingPrivacyPolicy;

    @BindView(com.joyn.me.R.id.setting_faq)
    LinearLayout settingFaq;


    @BindView(com.joyn.me.R.id.setting_rateThisApps)
    LinearLayout settingRateThisApps;

    @BindView(com.joyn.me.R.id.setting_logout)
    RelativeLayout settingLogout;

    private User loginUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(com.joyn.me.R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);


        settingEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UpdateProfileActivity.class));
            }
        });

        settingChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            }
        });

        settingTermOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TermOfServiceActivity.class));
            }
        });

        settingPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrivacyPolicyActivity.class));
            }
        });

        settingRateThisApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getActivity().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        settingFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FAQActivity.class));
            }
        });

        settingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = GoridemeAplication.getInstance(getContext()).getRealmInstance();
                realm.beginTransaction();
                realm.delete(User.class);
                realm.commitTransaction();
                GoridemeAplication.getInstance(getContext()).setLoginUser(null);
                startActivity(new Intent(getContext(), SplashActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loginUser = GoridemeAplication.getInstance(getActivity()).getLoginUser();
        settingProfileName.setText(String.format("%s %s", loginUser.getNamaDepan(), loginUser.getNamaBelakang()));
        settingProfileEmail.setText(loginUser.getEmail());
        settingProfilePhone.setText(loginUser.getNoTelepon());
    }
}
