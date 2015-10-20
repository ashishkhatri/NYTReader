package com.ashish.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsDetailActivity extends AppCompatActivity {

    public static final String EXTRA_URL = "url";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
        } else {
            String url = extras.getString(EXTRA_URL);
            mWebView.loadUrl(url);
        }
    }
}
