package com.sudanese.studentassistant;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PdfViewerActivity extends AppCompatActivity {
    private WebView pdfWebView;
    private ProgressBar loadingProgress;
    private TextView titleText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        
        initializeViews();
        loadPdf();
    }
    
    private void initializeViews() {
        pdfWebView = findViewById(R.id.pdfWebView);
        loadingProgress = findViewById(R.id.loadingProgress);
        titleText = findViewById(R.id.titleText);
        
        // إعداد WebView
        pdfWebView.getSettings().setJavaScriptEnabled(true);
        pdfWebView.getSettings().setBuiltInZoomControls(true);
        pdfWebView.getSettings().setDisplayZoomControls(false);
        pdfWebView.getSettings().setLoadWithOverviewMode(true);
        pdfWebView.getSettings().setUseWideViewPort(true);
        
        pdfWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                loadingProgress.setVisibility(android.view.View.VISIBLE);
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                loadingProgress.setVisibility(android.view.View.GONE);
            }
        });
    }
    
    private void loadPdf() {
        String title = getIntent().getStringExtra("resource_title");
        String pdfUrl = getIntent().getStringExtra("resource_url");
        
        titleText.setText(title);
        
        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            // تحميل PDF باستخدام Google Docs Viewer
            String googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=" + pdfUrl;
            pdfWebView.loadUrl(googleDocsUrl);
        } else {
            titleText.setText("رابط الملف غير متوفر");
        }
    }
    
    @Override
    public void onBackPressed() {
        if (pdfWebView.canGoBack()) {
            pdfWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}