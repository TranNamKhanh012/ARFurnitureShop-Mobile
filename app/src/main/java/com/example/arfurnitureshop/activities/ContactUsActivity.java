package com.example.arfurnitureshop.activities; // Thay bằng package name thực tế của bạn

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.arfurnitureshop.R;

public class ContactUsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvHeaderTitle;
    private CardView cardHotline, cardChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        // 1. Ánh xạ các view từ Header và Layout
        initViews();

        // 2. Cấu hình Header cho trang Contact Us
        setupHeader();

        // 3. Xử lý sự kiện click
        setupClickListeners();
    }

    private void initViews() {
        // Ánh xạ từ header được include (dùng đúng ID trong file layout_main_header hoặc layout_custom_header)
        ivBack = findViewById(R.id.ivMenu); // Nếu bạn dùng icon menu làm nút back
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle); // Nếu header có TextView title

        // Ánh xạ các nút liên hệ
        cardHotline = findViewById(R.id.cardHotline);
        cardChat = findViewById(R.id.cardChat);

        // Ẩn các icon không cần thiết trong header (nếu có)
        View ivSearch = findViewById(R.id.ivSearch);
        if (ivSearch != null) ivSearch.setVisibility(View.GONE);
    }

    private void setupHeader() {
        // Đổi icon Menu thành icon Back (nếu cần)
        ivBack.setImageResource(android.R.drawable.ic_menu_revert);
        ivBack.setRotation(0); // Trả về 0 độ nếu trước đó bạn xoay 90 độ

        // Đổi tiêu đề
        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText("Liên hệ hỗ trợ");
        }
    }

    private void setupClickListeners() {
        // Nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // Gọi điện Hotline
        cardHotline.setOnClickListener(v -> {
            String phone = "0123456789"; // Số điện thoại hỗ trợ của bạn
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        // Nhắn tin WhatsApp hoặc Zalo (Dùng link web để tự động mở App)
        cardChat.setOnClickListener(v -> {
            String url = "https://zalo.me/0123456789"; // Hoặc link WhatsApp
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
    }
}