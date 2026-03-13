package com.example.arfurnitureshop.activities; // Thay bằng package name thực tế của bạn

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Ánh xạ nút Back từ Header dùng chung
        // Lưu ý: Nếu header của bạn dùng ID ivMenu cho icon bên trái
        ImageView btnBack = findViewById(R.id.ivMenu);
        TextView tvTitle = findViewById(R.id.tvHeaderTitle);

        // Tùy chỉnh Header
        if (tvTitle != null) {
            tvTitle.setText("Về chúng tôi");
        }

        if (btnBack != null) {
            btnBack.setImageResource(android.R.drawable.ic_menu_revert);
            btnBack.setRotation(0);
            btnBack.setOnClickListener(v -> finish()); // Quay lại trang trước
        }

        // Ẩn các nút tìm kiếm/thông báo để trang "About" trông sạch sẽ hơn
        View searchIcon = findViewById(R.id.ivSearch);
        View notifyIcon = findViewById(R.id.ivNotification);
        if (searchIcon != null) searchIcon.setVisibility(View.GONE);
        if (notifyIcon != null) notifyIcon.setVisibility(View.GONE);
    }
}