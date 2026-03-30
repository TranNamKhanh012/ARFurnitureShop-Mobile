package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.arfurnitureshop.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // ==========================================
        // 1. ÁNH XẠ HEADER QUA THẺ INCLUDE (Chống lỗi Crash)
        // ==========================================
        View headerView = findViewById(R.id.headerAboutUs);
        if (headerView != null) {
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText("Về Chúng Tôi");
            }

            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
        }

        // ==========================================
        // 2. XỬ LÝ HIỂN THỊ BẢN ĐỒ BẰNG WEBVIEW
        // ==========================================
        WebView webViewMap = findViewById(R.id.webViewMap);
        if (webViewMap != null) {
            // Bật JavaScript
            WebSettings webSettings = webViewMap.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webViewMap.setWebViewClient(new WebViewClient());

            // 1. Lấy mã nhúng (iframe) từ Google Maps (bạn có thể thay bằng link iframe địa chỉ thật của bạn)
            String iframeHtml = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3724.096814183571!2d105.77972171540227!3d21.028811885998316!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x313454b32b842a37%3A0xe91a56573e7f9a11!2zSGFub2ksIFZpZXRuYW0!5e0!3m2!1sen!2s!4v1625581234567!5m2!1sen!2s\" width=\"100%\" height=\"100%\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\"></iframe>";

            // 2. Bọc iframe vào một trang HTML trống, ép margin/padding = 0 để nó tràn viền cho đẹp
            String customHtml = "<html><body style=\"margin: 0; padding: 0;\">" + iframeHtml + "</body></html>";

            // 3. Nạp đoạn HTML này vào WebView
            webViewMap.loadData(customHtml, "text/html", "utf-8");
        }
    }
}