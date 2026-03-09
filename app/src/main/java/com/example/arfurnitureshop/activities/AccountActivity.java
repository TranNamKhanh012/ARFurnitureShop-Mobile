package com.example.arfurnitureshop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    private LinearLayout layoutLoggedOut, layoutLoggedIn;
    private TextView tvProfileName, tvProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Ánh xạ View
        layoutLoggedOut = findViewById(R.id.layout_logged_out);
        layoutLoggedIn = findViewById(R.id.layout_logged_in);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);

        Button btnLoginNow = findViewById(R.id.btnLoginNow);
        Button btnRegisterNow = findViewById(R.id.btnRegisterNow);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Nút điều hướng
        btnLoginNow.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, LoginActivity.class)));
        btnRegisterNow.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, RegisterActivity.class)));

        // Nút Đăng xuất: Xóa bộ nhớ và tự động đẩy về Home
        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // 1. Xóa sạch dữ liệu đăng nhập
            editor.apply();

            // ==========================================
            // 2. DỌN SẠCH GIỎ HÀNG NỘI BỘ TRÊN ĐIỆN THOẠI
            com.example.arfurnitureshop.utils.CartManager.getInstance(AccountActivity.this).clear();
            // ==========================================

            Toast.makeText(AccountActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

            // Đăng xuất xong đá văng về trang chủ luôn cho chuẩn UX
            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // ==========================================
        // XỬ LÝ THANH ĐIỀU HƯỚNG DƯỚI CÙNG (BOTTOM NAVIGATION)
        // ==========================================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_account); // Làm sáng icon Tài khoản

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Chuyển về trang chủ
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Bỏ hiệu ứng giật màn hình
                finish();
                return true;
            }
            else if (itemId == R.id.nav_category) {
                // Chuyển sang trang Danh mục
                Intent intent = new Intent(AccountActivity.this, CategoryProductsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_cart) {
                // Chuyển sang Giỏ hàng (Bạn nhớ kiểm tra xem id trong menu có đúng là nav_cart không nhé)
                Intent intent = new Intent(AccountActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            // Nếu bạn có thêm nút Wishlist (nav_wishlist) thì mở comment đoạn dưới ra:
            /*
            else if (itemId == R.id.nav_wishlist) {
                Intent intent = new Intent(AccountActivity.this, WishlistActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            */
            else if (itemId == R.id.nav_account) {
                // Đang ở trang Tài khoản rồi thì không làm gì cả
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginState(); // Mỗi lần mở lại trang Tài khoản thì kiểm tra xem đã đăng nhập chưa
    }

    // Hàm kiểm tra và đổi giao diện
    private void checkLoginState() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false);

        if (isLoggedIn) {
            // Đã đăng nhập: Hiện phần 2, ẩn phần 1
            layoutLoggedOut.setVisibility(View.GONE);
            layoutLoggedIn.setVisibility(View.VISIBLE);

            // Lấy dữ liệu từ bộ nhớ điền vào TextView
            String fullName = sharedPreferences.getString("FULL_NAME", "Khách hàng");
            String username = sharedPreferences.getString("USERNAME", "");

            tvProfileName.setText(fullName);
            tvProfileEmail.setText(username);
        } else {
            // Chưa đăng nhập: Hiện phần 1, ẩn phần 2
            layoutLoggedOut.setVisibility(View.VISIBLE);
            layoutLoggedIn.setVisibility(View.GONE);
        }
    }
}