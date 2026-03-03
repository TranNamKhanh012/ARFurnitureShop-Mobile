package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // 1. Ánh xạ thanh Menu dưới cùng
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // 2. BẬT SÁNG icon Giỏ hàng (Cart) vì chúng ta đang ở trang này
        bottomNav.setSelectedItemId(R.id.nav_cart);

        // 3. Xử lý sự kiện khi bấm vào các nút
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Quay lại Trang chủ (MainActivity)
                Intent intent = new Intent(CartActivity.this, MainActivity.class);

                // Mẹo cực hay: Dùng lệnh này để không tạo ra nhiều trang chủ chồng lên nhau
                // gây nặng máy, nó sẽ gọi lại trang chủ cũ đã mở.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);
                finish(); // Đóng trang Giỏ hàng hiện tại lại cho nhẹ bộ nhớ
                return true;

            } else if (itemId == R.id.nav_cart) {
                // Đang ở Giỏ hàng rồi thì không cần chuyển trang
                return true;
            }

            // Tương lai bạn có thể làm tương tự cho nav_wishlist, nav_account...

            return false;
        });
    }
}