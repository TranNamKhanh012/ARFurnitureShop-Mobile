package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.arfurnitureshop.R;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // 1. Ánh xạ 3 nút bấm và nút Back
        ImageView ivBack = findViewById(R.id.ivBack);
        CardView cardHotline = findViewById(R.id.cardHotline);
        CardView cardZalo = findViewById(R.id.cardZalo);
        CardView cardTawkTo = findViewById(R.id.cardTawkTo);

        // 2. Xử lý nút quay lại
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // 3. Xử lý Gọi điện Hotline
        if (cardHotline != null) {
            cardHotline.setOnClickListener(v -> {
                String phone = "0123456789"; // Đổi thành số điện thoại thật của bạn
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            });
        }

        // 4. Xử lý Nhắn tin Zalo
        if (cardZalo != null) {
            cardZalo.setOnClickListener(v -> {
                String url = "https://zalo.me/0123456789"; // Đổi thành link zalo thật của bạn
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            });
        }

        // 5. Xử lý mở màn hình Chat Tawk.to
        if (cardTawkTo != null) {
            cardTawkTo.setOnClickListener(v -> {
                // Chuyển sang trang TawkToChatActivity mà bạn đã tạo ở bước trước
                Intent intent = new Intent(ContactUsActivity.this, TawkToChatActivity.class);
                startActivity(intent);
            });
        }
    }
}