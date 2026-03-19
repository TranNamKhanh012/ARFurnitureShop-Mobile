package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.arfurnitureshop.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // 1. Ánh xạ nút Back và bắt sự kiện đóng trang
        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // 2. Xử lý hiển thị Bản đồ bằng WebView
        WebView webViewMap = findViewById(R.id.webViewMap);
        if (webViewMap != null) {
            // Bật JavaScript
            WebSettings webSettings = webViewMap.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webViewMap.setWebViewClient(new WebViewClient());

            // 1. Lấy mã nhúng (iframe) từ Google Maps (bạn có thể thay bằng link iframe địa chỉ thật của bạn)
            String iframeHtml = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3725.2169025714797!2d105.84598491090016!3d20.983940780573512!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135ac41db98b665%3A0x408509d6f5a57e2c!2s123%20P.%20T%C3%A2n%20Mai!5e0!3m2!1svi!2s!4v1773737485343!5m2!1svi!2s\" width=\"600\" height=\"450\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>";

            // 2. Bọc iframe vào một trang HTML trống, ép margin/padding = 0 để nó tràn viền cho đẹp
            String customHtml = "<html><body style=\"margin: 0; padding: 0;\">" + iframeHtml + "</body></html>";

            // 3. Nạp đoạn HTML này vào WebView
            webViewMap.loadData(customHtml, "text/html", "utf-8");
        }
    }
}