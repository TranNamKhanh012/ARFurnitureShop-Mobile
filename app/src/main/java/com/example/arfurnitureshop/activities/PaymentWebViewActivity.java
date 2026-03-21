package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentWebViewActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        String url = getIntent().getStringExtra("VNPAY_URL");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Kiểm tra nếu VNPAY trả về link kết quả (phải khớp với ReturnUrl ở C#)
                if (url.contains("PaymentCallback")) {
                    if (url.contains("vnp_ResponseCode=00")) {
                        Toast.makeText(PaymentWebViewActivity.this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                        // Code để xóa giỏ hàng hoặc về trang chủ ở đây
                    } else {
                        Toast.makeText(PaymentWebViewActivity.this, "Giao dịch thất bại!", Toast.LENGTH_LONG).show();
                    }
                    finish(); // Đóng trang thanh toán
                    return true;
                }
                return false;
            }
        });

        webView.loadUrl(url);
    }
}