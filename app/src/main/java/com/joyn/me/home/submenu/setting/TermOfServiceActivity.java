package com.joyn.me.home.submenu.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermOfServiceActivity extends AppCompatActivity {
    @BindView(com.joyn.me.R.id.web_view)
    WebView webView;
    @BindView(com.joyn.me.R.id.btn_home)
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.joyn.me.R.layout.activity_term_of_service);
        ButterKnife.bind(this);

        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("http://indo.org/index.php/c_utama/SyaratKetentuanapp");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
