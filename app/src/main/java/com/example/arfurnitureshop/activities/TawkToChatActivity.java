package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.arfurnitureshop.R;

public class TawkToChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tawkto_chat);

        // 1. Nút quay lại
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // 2. Cấu hình WebView cho Tawk.to
        WebView webView = findViewById(R.id.webViewTawkTo);
        WebSettings webSettings = webView.getSettings();

        // Bắt buộc phải bật 2 dòng này thì Tawk.to mới chạy được
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Ép WebView mở link bên trong App, không bị văng ra trình duyệt Chrome
        webView.setWebViewClient(new WebViewClient());

        // 3. THAY ĐƯỜNG LINK DIRECT CHAT CỦA BẠN VÀO ĐÂY:
        String tawkToUrl = "https://tawk.to/chat/69bcca29600a121c36fa7ae7/1jk4nbdd5";

        webView.loadUrl(tawkToUrl);
    }
}