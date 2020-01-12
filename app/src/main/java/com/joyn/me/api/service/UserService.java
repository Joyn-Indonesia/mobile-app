package com.joyn.me.api.service;

import com.joyn.me.model.json.book.RateDriverRequestJson;
import com.joyn.me.model.json.fcm.CancelBookRequestJson;
import com.joyn.me.model.json.fcm.CancelBookResponseJson;
import com.joyn.me.model.json.menu.HelpResponseJson;
import com.joyn.me.model.json.menu.HistoryRequestJson;
import com.joyn.me.model.json.menu.VersionRequestJson;
import com.joyn.me.model.json.menu.VersionResponseJson;
import com.joyn.me.model.json.user.ChangePasswordRequestJson;
import com.joyn.me.model.json.user.ChangePasswordResponseJson;
import com.joyn.me.model.json.user.GetBannerResponseJson;
import com.joyn.me.model.json.user.GetFiturResponseJson;
import com.joyn.me.model.json.user.GetSaldoResponseJson;
import com.joyn.me.model.json.user.RegisterRequestJson;
import com.joyn.me.model.json.user.RegisterResponseJson;
import com.joyn.me.model.json.user.TopupRequestJson;
import com.joyn.me.model.json.user.TopupResponseJson;
import com.joyn.me.model.json.user.UpdateProfileResponseJson;
import com.joyn.me.model.json.book.RateDriverResponseJson;
import com.joyn.me.model.json.menu.HelpRequestJson;
import com.joyn.me.model.json.menu.HistoryResponseJson;
import com.joyn.me.model.json.user.GetSaldoRequestJson;
import com.joyn.me.model.json.user.LoginRequestJson;
import com.joyn.me.model.json.user.LoginResponseJson;
import com.joyn.me.model.json.user.UpdateProfileRequestJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;



public interface UserService {

    @POST("pelanggan/login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("pelanggan/register_user")
    Call<RegisterResponseJson> register(@Body RegisterRequestJson param);

    @POST("pelanggan/get_saldo")
    Call<GetSaldoResponseJson> getSaldo(@Body GetSaldoRequestJson param);

    @GET("pelanggan/detail_fitur")
    Call<GetFiturResponseJson> getFitur();

    @POST("pelanggan/user_send_help")
    Call<HelpResponseJson> sendHelp(@Body HelpRequestJson param);

    @POST("pelanggan/update_profile")
    Call<UpdateProfileResponseJson> updateProfile(@Body UpdateProfileRequestJson param);

    @POST("pelanggan/change_password")
    Call<ChangePasswordResponseJson> changePassword(@Body ChangePasswordRequestJson param);

    @POST("book/user_cancel_transaction")
    Call<CancelBookResponseJson> cancelOrder(@Body CancelBookRequestJson param);

    @POST("pelanggan/check_version")
    Call<VersionResponseJson> checkVersion(@Body VersionRequestJson param);

    @POST("book/user_rate_driver")
    Call<RateDriverResponseJson> rateDriver(@Body RateDriverRequestJson param);

    @POST("pelanggan/verifikasi_topup")
    Call<TopupResponseJson> topUp(@Body TopupRequestJson param);

    @POST("pelanggan/complete_transaksi")
    Call<HistoryResponseJson> getCompleteHistory(@Body HistoryRequestJson param);

    @POST("pelanggan/inprogress_transaksi")
    Call<HistoryResponseJson> getOnProgressHistory(@Body HistoryRequestJson param);

    @GET("pelanggan/banner_promosi")
    Call<GetBannerResponseJson> getBanner();

}
