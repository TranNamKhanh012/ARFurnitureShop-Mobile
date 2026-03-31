package com.example.arfurnitureshop.activities;

import android.content.Intent;
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
        // 1. ÁNH XẠ HEADER QUA THẺ INCLUDE
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
            // =====================================
            // THÊM SỰ KIỆN NÚT HOME VÀO ĐÂY
            // =====================================
            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(this, MainActivity.class); // Về trang chủ
                    // Xóa toàn bộ lịch sử các trang trước đó để tránh đầy RAM
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        // ==========================================
        // 2. XỬ LÝ HIỂN THỊ BẢN ĐỒ BẰNG WEBVIEW
        // ==========================================
        WebView webViewMap = findViewById(R.id.webViewMap);
        if (webViewMap != null) {
            WebSettings webSettings = webViewMap.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webViewMap.setWebViewClient(new WebViewClient());

            String iframeHtml = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3724.096814183571!2d105.77972177609204!3d21.028811880620857!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ab86cece9ac1%3A0xa9bc04e04602dd85!2zRlBUIFBvbHl0ZWNobmljIEjDoCBO4buZaQ!5e0!3m2!1svi!2s!4v1710928929747!5m2!1svi!2s\" width=\"100%\" height=\"100%\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>";
            String customHtml = "<html><body style=\"margin: 0; padding: 0;\">" + iframeHtml + "</body></html>";

            webViewMap.loadData(customHtml, "text/html", "utf-8");
        }
    }
}