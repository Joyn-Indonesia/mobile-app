package com.joyn.me.signIn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.joyn.me.GoridemeAplication;
import com.joyn.me.api.ServiceGenerator;
import com.joyn.me.api.service.UserService;
import com.joyn.me.home.login;
import com.joyn.me.model.FirebaseToken;
import com.joyn.me.model.User;
import com.joyn.me.model.json.user.LoginRequestJson;
import com.joyn.me.model.json.user.LoginResponseJson;
import com.joyn.me.signUp.SignUpActivity;
import com.joyn.me.utils.DialogActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class SignInActivity extends DialogActivity implements Validator.ValidationListener {

    private static final String TAG = "SignInActivity";

    @NotEmpty
    @Email
    @BindView(com.joyn.me.R.id.signIn_email)
    EditText textEmail;

    @NotEmpty
    @BindView(com.joyn.me.R.id.signIn_password)
    EditText textPassword;

    @BindView(com.joyn.me.R.id.signIn_signInButton)
    Button buttonSignIn;

    @BindView(com.joyn.me.R.id.signIn_signUpButton)
    LinearLayout buttonSignUp;

    @BindView(com.joyn.me.R.id.forgotpass)
    TextView bForgot;

    Validator validator;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_sign_in);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivityForResult(intent, SignUpActivity.SIGNUP_ID);
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });
    }

    private void onSignInClick() {
        showProgressDialog(com.joyn.me.R.string.dialog_loading);
        LoginRequestJson request = new LoginRequestJson();
        request.setEmail(textEmail.getText().toString());
        request.setPassword(textPassword.getText().toString());

        Realm realm = Realm.getDefaultInstance();
        FirebaseToken token = realm.where(FirebaseToken.class).findFirst();
        if (token.getTokenId() != null) {
            request.setRegId(token.getTokenId());
        } else {
            Toast.makeText(this, com.joyn.me.R.string.waiting_pleaseWait, Toast.LENGTH_SHORT).show();
            hideProgressDialog();
            return;
        }

        UserService service = ServiceGenerator.createService(UserService.class, request.getEmail(), request.getPassword());
        service.login(request).enqueue(new Callback<LoginResponseJson>() {
            @Override
            public void onResponse(Call<LoginResponseJson> call, Response<LoginResponseJson> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().getMessage().equalsIgnoreCase("found")) {
                        User user = response.body().getData().get(0);

                        saveUser(user);

                        Intent intent = new Intent(SignInActivity.this, login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Username atau Password salah", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponseJson> call, Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                Toast.makeText(SignInActivity.this, "System error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignUpActivity.SIGNUP_ID) {
            if (resultCode == Activity.RESULT_OK) {
                User user = (User) data.getSerializableExtra(SignUpActivity.USER_KEY);

                saveUser(user);

                Intent intent = new Intent(SignInActivity.this, login.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onValidationSucceeded() {
        onSignInClick();
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

    private void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(user);
        realm.commitTransaction();

        GoridemeAplication.getInstance(SignInActivity.this).setLoginUser(user);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FirebaseToken response) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FirebaseToken.class);
        realm.copyToRealm(response);
        realm.commitTransaction();
    }

    public void forgot(View view) {
        Intent intent = new Intent(SignInActivity.this, ForgotActivity.class);
        startActivity(intent);
    }

}
