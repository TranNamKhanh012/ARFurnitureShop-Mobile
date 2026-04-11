package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.net.Uri;
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

            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        // ==========================================
        // 2. BẤM VÀO ĐỊA CHỈ ĐỂ MỞ APP GOOGLE MAPS
        // ==========================================
        TextView tvLocationAddress = findViewById(R.id.tvLocationAddress);
        if (tvLocationAddress != null) {
            tvLocationAddress.setOnClickListener(v -> {
                // Link tìm kiếm Hà Nội, Việt Nam trên Google Maps
                String mapUrl = "https://www.google.com/maps/search/?api=1&query=123+Tân+Mai,Hoàng+Mai,Hà+Nội,+Việt+Nam";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
                startActivity(intent);
            });
        }

        // ==========================================
        // 3. XỬ LÝ HIỂN THỊ BẢN ĐỒ BẰNG WEBVIEW (ĐÃ SỬA LINK)
        // ==========================================
        WebView webViewMap = findViewById(R.id.webViewMap);
        if (webViewMap != null) {
            WebSettings webSettings = webViewMap.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webViewMap.setWebViewClient(new WebViewClient());

            // ĐÃ SỬA: Dùng iframe chuẩn của Google Maps (Tọa độ Hà Nội)
            String iframeHtml = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3725.219189280141!2d105.8457294109003!3d20.983849080573613!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ac41dcde2611%3A0x9b57bf4c78ab1b30!2zMTIzIFAuIFTDom4gTWFpLCBUxrDGoW5nIE1haSwgSMOgIE7hu5lpLCBWaeG7h3QgTmFt!5e0!3m2!1svi!2s!4v1775808368935!5m2!1svi!2s\" width=\"600\" height=\"450\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>";
            String customHtml = "<html><body style=\"margin: 0; padding: 0;\">" + iframeHtml + "</body></html>";

            webViewMap.loadData(customHtml, "text/html", "utf-8");
        }
    }
}