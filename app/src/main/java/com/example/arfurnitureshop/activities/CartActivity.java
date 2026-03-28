package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.CartAdapter;
import com.example.arfurnitureshop.utils.CartManager;
import com.example.arfurnitureshop.utils.MenuHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {
    // PHẢI KHAI BÁO BIẾN Ở ĐÂY ĐỂ HẾT ĐỎ
    private RecyclerView rv;
    private TextView tvTotal;
    private Button btnContinue;
    private double currentTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ánh xạ đúng ID từ file XML bạn vừa gửi
        rv = findViewById(R.id.rvCartItems);
        tvTotal = findViewById(R.id.tvTotalAmount);
        btnContinue = findViewById(R.id.btnContinue); // Khớp với android:id="@+id/btnContinue"

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new CartAdapter(CartManager.getInstance(this).getItems(), this::updateTotal));

        updateTotal();

        // XỬ LÝ SỰ KIỆN NÚT CONTINUE (THANH TOÁN)
        btnContinue.setOnClickListener(v -> {
            if (currentTotal > 0) {
                // Chuyển sang trang Checkout xác nhận
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("TOTAL_PRICE", currentTotal);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            }
        });

        // ==========================================
        // GỌI TRỢ LÝ MENU RA LÀM VIỆC (CHỈ 3 DÒNG CODE)
        // ==========================================
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        ImageView ivMenu = findViewById(R.id.ivMenu);

        // Giao toàn bộ việc đóng/mở menu cho MenuHelper xử lý
        MenuHelper.setupMenu(this, drawerLayout, ivMenu, navigationView);
        // ==========================================
        // XỬ LÝ THANH MENU FOOTER (BOTTOM NAVIGATION)
        // ==========================================
        // ==========================================
        // THANH ĐIỀU HƯỚNG DƯỚI CÙNG (CHUYỂN TRANG SIÊU MƯỢT, KHÔNG CHỚP NHÁY)
        // ==========================================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // [QUAN TRỌNG] TÙY CHỈNH CHO TỪNG TRANG:
        // Ở trang nào thì bạn đổi ID thành icon của trang đó nhé!
        // (Ví dụ: Trang chủ -> R.id.nav_home | Giỏ hàng -> R.id.nav_cart | Tài khoản -> R.id.nav_account)
        bottomNav.setSelectedItemId(R.id.nav_cart);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Nếu người dùng bấm lại vào chính cái tab đang xem -> Đứng im, không load lại trang
            if (itemId == bottomNav.getSelectedItemId()) {
                return true;
            }

            Intent intent = null;

            if (itemId == R.id.nav_home) {
                intent = new Intent(this, MainActivity.class);
            } else if (itemId == R.id.nav_category) {
                // Nhớ đổi tên file ở đây nếu trang danh mục của bạn tên là CategoryProductsActivity nhé
                intent = new Intent(this, AllCategoriesActivity.class);
            } else if (itemId == R.id.nav_cart) {
                intent = new Intent(this, CartActivity.class);
            } else if (itemId == R.id.nav_wishlist) {
                intent = new Intent(this, WishlistActivity.class);
            } else if (itemId == R.id.nav_account) {
                intent = new Intent(this, AccountActivity.class);
            }

            if (intent != null) {
                // 1. Tắt hiệu ứng tạo màn hình mới
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

                // 2. Tắt triệt để hiệu ứng trượt/nhảy của Android
                overridePendingTransition(0, 0);

                // 3. Đóng trang cũ để giải phóng RAM cho điện thoại
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

        // ... (Các code cũ của bạn nếu có) ...

        // TỰ ĐỘNG ĐỒNG BỘ CHẤM ĐỎ GIỎ HÀNG VÀ YÊU THÍCH
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        com.example.arfurnitureshop.utils.BadgeUtils.loadCachedBadges(this, bottomNav);
    }

    // PHẢI CÓ HÀM NÀY ĐỂ HẾT LỖI "this::updateTotal"
    private void updateTotal() {
        DecimalFormat df = new DecimalFormat("#,###");
        double total = CartManager.getInstance(this).getTotal();

        // [QUAN TRỌNG]: Bắt buộc phải cập nhật biến này thì nút Thanh toán mới chạy
        currentTotal = total;

        tvTotal.setText("₫ " + df.format(total) + " VND");
    }
}