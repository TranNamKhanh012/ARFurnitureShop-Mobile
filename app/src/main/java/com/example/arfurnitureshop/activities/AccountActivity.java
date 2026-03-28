package com.example.arfurnitureshop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    private LinearLayout layoutLoggedOut;
    private View layoutLoggedIn; // ScrollView trong XML mới
    private TextView tvUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // ==========================================
        // 1. ÁNH XẠ VIEW
        // ==========================================
        layoutLoggedOut = findViewById(R.id.layout_logged_out);
        layoutLoggedIn = findViewById(R.id.layout_logged_in);

        // Các view của phần Chưa đăng nhập
        Button btnLoginNow = findViewById(R.id.btnLoginNow);
        Button btnRegisterNow = findViewById(R.id.btnRegisterNow);

        // Các view của phần Đã đăng nhập (Minimalist UI)
        tvUserEmail = findViewById(R.id.tvUserEmail);
        ImageView ivEditProfile = findViewById(R.id.ivEditProfile);
        LinearLayout btnMyAddresses = findViewById(R.id.btnMyAddresses);
        LinearLayout btnOrderHistory = findViewById(R.id.btnOrderHistory);
        LinearLayout btnSignOut = findViewById(R.id.btnSignOut);

        // ==========================================
        // 2. SỰ KIỆN CÁC NÚT BẤM
        // ==========================================

        // Nhóm nút Chưa đăng nhập
        btnLoginNow.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, LoginActivity.class)));
        btnRegisterNow.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, RegisterActivity.class)));

        // Nhóm nút Đã đăng nhập
        ivEditProfile.setOnClickListener(v -> {
            // Chuyển thẳng sang trang ProfileActivity
            Intent intent = new Intent(AccountActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnMyAddresses.setOnClickListener(v -> {
            // Chuyển đến trang Địa chỉ của tôi
            startActivity(new android.content.Intent(AccountActivity.this, MyAddressesActivity.class));
        });

        // Giả sử trong activity_account.xml, phần lịch sử mua hàng của bạn có ID là layoutOrderHistory hoặc btnOrderHistory
        View layoutOrderHistory = findViewById(R.id.btnOrderHistory); // <--- Đổi thành đúng ID của bạn

        layoutOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });

        // Nút Đăng xuất: Xóa bộ nhớ và tự động đẩy về Home
        btnSignOut.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // 1. Xóa sạch dữ liệu đăng nhập
            editor.apply();

            // 2. DỌN SẠCH GIỎ HÀNG VÀ WISHLIST NỘI BỘ TRÊN ĐIỆN THOẠI
            com.example.arfurnitureshop.utils.CartManager.getInstance(AccountActivity.this).clear();
            com.example.arfurnitureshop.models.WishlistManager.clear();

            Toast.makeText(AccountActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

            // Đăng xuất xong đá văng về trang chủ luôn cho chuẩn UX
            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Ánh xạ DrawerLayout và NavigationView mới thêm vào XML
        androidx.drawerlayout.widget.DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        com.google.android.material.navigation.NavigationView navigationView = findViewById(R.id.navigationView);
        android.widget.ImageView ivMenu = findViewById(R.id.ivMenu);

        // Gọi Trợ lý để xử lý Sidebar
        com.example.arfurnitureshop.utils.MenuHelper.setupMenu(this, drawerLayout, ivMenu, navigationView);

        // ==========================================
        // 3. THANH ĐIỀU HƯỚNG DƯỚI CÙNG (BOTTOM NAV)
        // ==========================================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_account);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == bottomNav.getSelectedItemId()) {
                return true;
            }

            Intent intent = null;

            if (itemId == R.id.nav_home) {
                intent = new Intent(this, MainActivity.class);
            } else if (itemId == R.id.nav_category) {
                intent = new Intent(this, AllCategoriesActivity.class);
            } else if (itemId == R.id.nav_cart) {
                intent = new Intent(this, CartActivity.class);
            } else if (itemId == R.id.nav_wishlist) {
                intent = new Intent(this, WishlistActivity.class);
            } else if (itemId == R.id.nav_account) {
                intent = new Intent(this, AccountActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });
        // GỌI TRỢ LÝ TÌM KIẾM RA LÀM VIỆC
        com.example.arfurnitureshop.utils.SearchHelper.setupSearch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginState();
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        com.example.arfurnitureshop.utils.BadgeUtils.loadCachedBadges(this, bottomNav);
    }

    // ==========================================
    // 4. HÀM KIỂM TRA VÀ ĐỔI GIAO DIỆN
    // ==========================================
    private void checkLoginState() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false);

        if (isLoggedIn) {
            // Đã đăng nhập: Hiện giao diện mới, ẩn nút Login
            layoutLoggedOut.setVisibility(View.GONE);
            layoutLoggedIn.setVisibility(View.VISIBLE);

            // Lấy Email/Username từ bộ nhớ để hiển thị lên giao diện mới
            String username = sharedPreferences.getString("USERNAME", "Người dùng");
            tvUserEmail.setText(username);
        } else {
            // Chưa đăng nhập: Hiện nút Login, ẩn giao diện thông tin
            layoutLoggedOut.setVisibility(View.VISIBLE);
            layoutLoggedIn.setVisibility(View.GONE);
        }
    }
}