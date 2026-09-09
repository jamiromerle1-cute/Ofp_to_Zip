package com.example.launcher;

import android.app.AlertDialog;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String currentSearchEngine = "https://www.google.com/search?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        Button btnRoblox = findViewById(R.id.btnRoblox);
        Button btnTiktok = findViewById(R.id.btnTiktok);
        Button btnYoutube = findViewById(R.id.btnYoutube);
        Button btnWebSearch = findViewById(R.id.btnWebSearch);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("https://www.roblox.com");

        btnRoblox.setOnClickListener(v -> webView.loadUrl("https://www.roblox.com"));
        btnTiktok.setOnClickListener(v -> webView.loadUrl("https://www.tiktok.com"));
        btnYoutube.setOnClickListener(v -> webView.loadUrl("https://www.youtube.com"));

        btnWebSearch.setOnClickListener(v -> {
            String[] engines = {"Google", "Yahoo", "Bing", "DuckDuckGo"};
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Select your web engine");
            builder.setItems(engines, (dialog, which) -> {
                switch (which) {
                    case 0: currentSearchEngine = "https://www.google.com/search?q="; break;
                    case 1: currentSearchEngine = "https://search.yahoo.com/search?p="; break;
                    case 2: currentSearchEngine = "https://www.bing.com/search?q="; break;
                    case 3: currentSearchEngine = "https://duckduckgo.com/?q="; break;
                }
                webView.loadUrl(currentSearchEngine);
            });
            builder.show();
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
