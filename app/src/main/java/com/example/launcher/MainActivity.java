package com.example.launcher;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollView tabBarLayout;
    private View menuScrollView;
    private LinearLayout urlBarLayout;
    private EditText etUrlInput;
    private View webContainer;
    
    private WebView webViewRoblox, webViewTiktok, webViewYoutube, webViewEngine;
    private WebView activeWebView;
    private String currentSearchEngine = "https://www.google.com/search?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabBarLayout = findViewById(R.id.tabBarLayout);
        menuScrollView = findViewById(R.id.menuScrollView);
        urlBarLayout = findViewById(R.id.urlBarLayout);
        etUrlInput = findViewById(R.id.etUrlInput);
        webContainer = findViewById(R.id.webContainer);
        Button btnGo = findViewById(R.id.btnGo);

        webViewRoblox = findViewById(R.id.webViewRoblox);
        webViewTiktok = findViewById(R.id.webViewTiktok);
        webViewYoutube = findViewById(R.id.webViewYoutube);
        webViewEngine = findViewById(R.id.webViewEngine);

        Button btnRoblox = findViewById(R.id.btnRoblox);
        Button btnTiktok = findViewById(R.id.btnTiktok);
        Button btnYoutube = findViewById(R.id.btnYoutube);
        Button btnWebSearch = findViewById(R.id.btnWebSearch);

        Button tabRoblox = findViewById(R.id.tabRoblox);
        Button tabTiktok = findViewById(R.id.tabTiktok);
        Button tabYoutube = findViewById(R.id.tabYoutube);
        Button tabWebSearch = findViewById(R.id.tabWebSearch);
        Button tabHome = findViewById(R.id.tabHome);

        setupWebView(webViewRoblox);
        setupWebView(webViewTiktok);
        setupWebView(webViewYoutube);
        setupWebView(webViewEngine);

        // Menu Button Clicks
        btnRoblox.setOnClickListener(v -> showTab(webViewRoblox, "https://www.roblox.com"));
        btnTiktok.setOnClickListener(v -> showTab(webViewTiktok, "https://www.tiktok.com"));
        btnYoutube.setOnClickListener(v -> showTab(webViewYoutube, "https://www.youtube.com"));

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
                showTab(webViewEngine, currentSearchEngine.replace("search?q=", "").replace("search?p=", ""));
            });
            builder.show();
        });

        // Tab Switch Clicks
        tabRoblox.setOnClickListener(v -> switchTab(webViewRoblox));
        tabTiktok.setOnClickListener(v -> switchTab(webViewTiktok));
        tabYoutube.setOnClickListener(v -> switchTab(webViewYoutube));
        tabWebSearch.setOnClickListener(v -> switchTab(webViewEngine));
        tabHome.setOnClickListener(v -> showHomeMenu());

        btnGo.setOnClickListener(v -> performSearchOrNav());
        etUrlInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                performSearchOrNav();
                return true;
            }
            return false;
        });
    }

    private void setupWebView(WebView wv) {
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (view == activeWebView) {
                    etUrlInput.setText(url);
                }
            }
        });
    }

    private void showTab(WebView targetWebView, String defaultUrl) {
        menuScrollView.setVisibility(View.GONE);
        tabBarLayout.setVisibility(View.VISIBLE);
        urlBarLayout.setVisibility(View.VISIBLE);
        webContainer.setVisibility(View.VISIBLE);

        webViewRoblox.setVisibility(View.GONE);
        webViewTiktok.setVisibility(View.GONE);
        webViewYoutube.setVisibility(View.GONE);
        webViewEngine.setVisibility(View.GONE);

        targetWebView.setVisibility(View.VISIBLE);
        activeWebView = targetWebView;

        if (targetWebView.getUrl() == null || targetWebView.getUrl().equals("about:blank")) {
            targetWebView.loadUrl(defaultUrl);
        } else {
            etUrlInput.setText(targetWebView.getUrl());
        }
    }

    private void switchTab(WebView targetWebView) {
        webViewRoblox.setVisibility(View.GONE);
        webViewTiktok.setVisibility(View.GONE);
        webViewYoutube.setVisibility(View.GONE);
        webViewEngine.setVisibility(View.GONE);

        targetWebView.setVisibility(View.VISIBLE);
        activeWebView = targetWebView;
        if (targetWebView.getUrl() != null) {
            etUrlInput.setText(targetWebView.getUrl());
        }
    }

    private void showHomeMenu() {
        webContainer.setVisibility(View.GONE);
        urlBarLayout.setVisibility(View.GONE);
        tabBarLayout.setVisibility(View.GONE);
        menuScrollView.setVisibility(View.VISIBLE);
    }

    private void performSearchOrNav() {
        if (activeWebView == null) return;
        String query = etUrlInput.getText().toString().trim();
        if (query.isEmpty()) return;

        if (query.startsWith("http://") || query.startsWith("https://") || query.contains(".com") || query.contains(".org") || query.contains(".net")) {
            if (!query.startsWith("http://") && !query.startsWith("https://")) {
                query = "https://" + query;
            }
            activeWebView.loadUrl(query);
        } else {
            activeWebView.loadUrl(currentSearchEngine + query);
        }
    }

    @Override
    public void onBackPressed() {
        if (activeWebView != null && activeWebView.getVisibility() == View.VISIBLE && activeWebView.canGoBack()) {
            activeWebView.goBack();
        } else if (webContainer.getVisibility() == View.VISIBLE) {
            showHomeMenu();
        } else {
            super.onBackPressed();
        }
    }
}
