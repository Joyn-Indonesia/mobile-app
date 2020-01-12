package com.joyn.me.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.joyn.me.model.FirebaseToken;

import org.greenrobot.eventbus.EventBus;



public class GoridemeInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        saveToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void saveToken(String tokenId) {
        FirebaseToken token = new FirebaseToken(tokenId);
        EventBus.getDefault().postSticky(token);
    }

}
