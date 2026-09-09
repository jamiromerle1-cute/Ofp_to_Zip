package com.example.launcher;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private View menuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        menuLayout = findViewById(R.id.menuLayout);

        Button btnRoblox = findViewById(R.id.btnRoblox);
        Button btnTiktok = findViewById(R.id.btnTiktok);
        Button btnYoutube = findViewById(R.id.btnYoutube);
        Button btnWebSearch = findViewById(R.id.btnWebSearch);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        btnRoblox.setOnClickListener(v -> openWebsite("https://www.roblox.com"));
        btnTiktok.setOnClickListener(v -> openWebsite("https://www.tiktok.com"));
        btnYoutube.setOnClickListener(v -> openWebsite("https://www.youtube.com"));

        btnWebSearch.setOnClickListener(v -> {
            String[] engines = {"Google", "Yahoo", "Bing", "DuckDuckGo"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Select your web engine");
            builder.setItems(engines, (dialog, which) -> {
                String url = "https://www.google.com";
                switch (which) {
                    case 0: url = "https://www.google.com"; break;
                    case 1: url = "https://search.yahoo.com"; break;
                    case 2: url = "https://www.bing.com"; break;
                    case 3: url = "https://duckduckgo.com"; break;
                }
                openWebsite(url);
            });
            builder.show();
        });
    }

    private void openWebsite(String url) {
        menuLayout.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = webView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        webView.setLayoutParams(params);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
        } else if (webView.getVisibility() == View.VISIBLE) {
            webView.setVisibility(View.GONE);
            webView.loadUrl("about:blank");
            ViewGroup.LayoutParams params = webView.getLayoutParams();
            params.height = 0;
            webView.setLayoutParams(params);
            menuLayout.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}
