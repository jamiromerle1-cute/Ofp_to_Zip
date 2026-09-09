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
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private View menuScrollView;
    private LinearLayout urlBarLayout;
    private EditText etUrlInput;
    private String currentSearchEngine = "https://www.google.com/search?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        menuScrollView = findViewById(R.id.menuScrollView);
        urlBarLayout = findViewById(R.id.urlBarLayout);
        etUrlInput = findViewById(R.id.etUrlInput);
        Button btnGo = findViewById(R.id.btnGo);

        Button btnRoblox = findViewById(R.id.btnRoblox);
        Button btnTiktok = findViewById(R.id.btnTiktok);
        Button btnYoutube = findViewById(R.id.btnYoutube);
        Button btnWebSearch = findViewById(R.id.btnWebSearch);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                etUrlInput.setText(url);
            }
        });

        btnRoblox.setOnClickListener(v -> openFullscreenWebsite("https://www.roblox.com"));
        btnTiktok.setOnClickListener(v -> openFullscreenWebsite("https://www.tiktok.com"));
        btnYoutube.setOnClickListener(v -> openFullscreenWebsite("https://www.youtube.com"));

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
                openFullscreenWebsite(currentSearchEngine.replace("search?q=", "").replace("search?p=", ""));
            });
            builder.show();
        });

        btnGo.setOnClickListener(v -> performSearchOrNav());
        etUrlInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                performSearchOrNav();
                return true;
            }
            return false;
        });
    }

    private void openFullscreenWebsite(String url) {
        menuScrollView.setVisibility(View.GONE);
        urlBarLayout.setVisibility(View.VISIBLE); // Ipapakita na ang Enter URL bar sa itaas
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    private void performSearchOrNav() {
        String query = etUrlInput.getText().toString().trim();
        if (query.isEmpty()) return;

        if (query.startsWith("http://") || query.startsWith("https://") || query.contains(".com") || query.contains(".org") || query.contains(".net")) {
            if (!query.startsWith("http://") && !query.startsWith("https://")) {
                query = "https://" + query;
            }
            webView.loadUrl(query);
        } else {
            webView.loadUrl(currentSearchEngine + query);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
        } else if (webView.getVisibility() == View.VISIBLE) {
            webView.setVisibility(View.GONE);
            urlBarLayout.setVisibility(View.GONE);
            webView.loadUrl("about:blank");
            menuScrollView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}
