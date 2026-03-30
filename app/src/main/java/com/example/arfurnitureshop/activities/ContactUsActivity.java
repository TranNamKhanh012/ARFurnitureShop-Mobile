package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.arfurnitureshop.R;

public class ContactUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // ==========================================
        // 1. ÁNH XẠ VÀ CÀI ĐẶT HEADER DÙNG CHUNG
        // ==========================================
        // Bước 1: Tìm cái thẻ <include> trước
        android.view.View headerView = findViewById(R.id.headerContactUs);

        if (headerView != null) {
            // Bước 2: Tìm Tiêu đề và Nút Back BÊN TRONG thẻ include đó
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText("Liên hệ hỗ trợ");
            }

            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
        }

        // ==========================================
        // 2. ÁNH XẠ VÀ XỬ LÝ CÁC NÚT BẤM LIÊN HỆ
        // ==========================================
        CardView cardHotline = findViewById(R.id.cardHotline);
        CardView cardZalo = findViewById(R.id.cardZalo);
        CardView cardTawkTo = findViewById(R.id.cardTawkTo);

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