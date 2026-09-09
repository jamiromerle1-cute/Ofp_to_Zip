package com.example.launcher;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private EditText urlInput;
    private Spinner engineSpinner;
    private final Set<String> blockedDomains = new HashSet<>();
    private boolean isAdBlockEnabled = true;
    private boolean isDesktopMode = false;

    private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Ad Blocker domains
        blockedDomains.add("googlesyndication.com");
        blockedDomains.add("adservice.google.com");
        blockedDomains.add("doubleclick.net");
        blockedDomains.add("connect.facebook.net");

        webView = findViewById(R.id.webView);
        urlInput = findViewById(R.id.urlInput);
        engineSpinner = findViewById(R.id.engineSpinner);
        Button btnGo = findViewById(R.id.btnGo);
        Button btnYoutube = findViewById(R.id.btnYoutube);
        Button btnTiktok = findViewById(R.id.btnTiktok);
        Button btnRoblox = findViewById(R.id.btnRoblox);

        String[] engines = {"Google", "Yahoo", "Bing", "DuckDuckGo"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, engines);
        engineSpinner.setAdapter(adapter);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (!isAdBlockEnabled) {
                    return super.shouldInterceptRequest(view, request);
                },
                String url = request.getUrl().toString().toLowerCase();
                for (String domain : blockedDomains) {
                    if (url.contains(domain)) {
                        return new WebResourceResponse("text/plain", "utf-8", null);
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("tiktok.com")) {
                    setDesktopMode(true);
                } else {
                    setDesktopMode(false);
                }
            }
        });

        webView.loadUrl("https://www.google.com");

        btnGo.setOnClickListener(v -> {
            String query = urlInput.getText().toString().trim();
            if (!query.isEmpty()) {
                String searchUrl;
                if (query.startsWith("http://") || query.startsWith("https://")) {
                    searchUrl = query;
                } else if (query.contains(".")) {
                    searchUrl = "https://" + query;
                } else {
                    String selectedEngine = engineSpinner.getSelectedItem().toString();
                    String searchBase = "https://www.google.com/search?q=";
                    if (selectedEngine.equals("Yahoo")) searchBase = "https://search.yahoo.com/search?p=";
                    else if (selectedEngine.equals("Bing")) searchBase = "https://www.bing.com/search?q=";
                    else if (selectedEngine.equals("DuckDuckGo")) searchBase = "https://duckduckgo.com/?q=";
                    searchUrl = searchBase + query;
                }
                webView.loadUrl(searchUrl);
            }
        });

        btnYoutube.setOnClickListener(v -> webView.loadUrl("https://www.youtube.com"));
        btnTiktok.setOnClickListener(v -> webView.loadUrl("https://www.tiktok.com"));
        btnRoblox.setOnClickListener(v -> webView.loadUrl("https://www.roblox.com"));
    }

    private void setDesktopMode(boolean enabled) {
        isDesktopMode = enabled;
        WebSettings webSettings = webView.getSettings();
        if (enabled) {
            webSettings.setUserAgentString(DESKTOP_USER_AGENT);
        } else {
            webSettings.setUserAgentString(MOBILE_USER_AGENT);
        }
        webView.reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle_desktop) {
            setDesktopMode(!isDesktopMode);
            Toast.makeText(this, "Desktop Site: " + (isDesktopMode ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_toggle_adblock) {
            isAdBlockEnabled = !isAdBlockEnabled;
            Toast.makeText(this, "Ad Blocker: " + (isAdBlockEnabled ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
