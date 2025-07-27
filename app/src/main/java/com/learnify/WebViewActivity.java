package com.learnify;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView webView = findViewById(R.id.webView);

        // Get the URL passed from the RoadmapFragment
        String url = getIntent().getStringExtra("URL");

        // Enable JavaScript in the WebView
        webView.getSettings().setJavaScriptEnabled(true);

        // Set WebViewClient to open links within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Load the Java roadmap URL
        if (url != null) {
            webView.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        WebView webView = findViewById(R.id.webView);

        // Check if the WebView can go back
        if (webView.canGoBack()) {
            webView.goBack();  // Go back within the WebView
        } else {
            super.onBackPressed();  // Default behavior (exit the activity)
        }
    }
}
