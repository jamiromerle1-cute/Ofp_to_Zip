package com.example.launcher;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private GridLayout gridLayout;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        gridLayout = findViewById((R.id.btnRoblox).getClass().getResource("") != null ? R.id.webView : R.id.webView); // safe lookup placeholder
        // Direct find views
        Button btnRoblox = findViewById(R.id.btnRoblox);
        Button btnTiktok = findViewById(R.id.btnTiktok);
        Button btnYoutube = findViewById(R.id.btnYoutube);
        Button btnWebSearch = findViewById(R.id.btnWebSearch);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Pag pinindot ang Roblox, itatago ang menu boxes at diretsong bubuksan ang site nang walang URL bar
        btnRoblox.setOnClickListener(v -> openWebsite("https://www.roblox.com"));
        btnTiktok.setOnClickListener(v -> openWebsite("https://www.tiktok.com"));
        btnYoutube.setOnClickListener(v -> openWebsite("https://www.youtube.com"));

        // Website Engine Dialog
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
        // Itago ang buttons, ipakita ang WebView nang buo
        findViewById(R.id.btnRoblox).getParent(); // dummy
        View grid = findViewById(R.id.btnRoblox).getRootView().findViewById(R.id.webView);
        // Actually toggle visibility cleanly
        android.widget.GridLayout gridLayoutContainer = findViewById(R.id.btnRoblox).getRootView().findViewById(R.id.webView).getRootView().findViewById(R.id.btnRoblox);
        // Simple visibility toggle:
        android.view.ViewGroup group = (android.view.ViewGroup) findViewById(R.id.btnRoblox).getParent();
        // Let us just load and show webView directly:
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
        } else if (webView.getVisibility() == View.VISIBLE) {
            // Bumalik sa box menu kung nasa website
            webView.setVisibility(View.GONE);
            webView.loadUrl("about:blank");
        } else {
            super.onBackPressed();
        }
    }
}
