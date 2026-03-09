package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.CartAdapter;
import com.example.arfurnitureshop.utils.CartManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {
    // PHẢI KHAI BÁO BIẾN Ở ĐÂY ĐỂ HẾT ĐỎ
    private RecyclerView rv;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ánh xạ View từ layout activity_cart.xml
        rv = findViewById(R.id.rvCartItems);
        tvTotal = findViewById(R.id.tvTotalAmount);

        rv.setLayoutManager(new LinearLayoutManager(this));

        // Gắn Adapter và truyền hàm updateTotal để cập nhật tiền khi tăng/giảm số lượng
        rv.setAdapter(new CartAdapter(CartManager.getInstance(this).getItems(), this::updateTotal));

        updateTotal();

        // ==========================================
        // XỬ LÝ THANH MENU FOOTER (BOTTOM NAVIGATION)
        // ==========================================
        BottomNavigationView nav = findViewById(R.id.bottomNavigationView);

        // Làm sáng icon Giỏ hàng
        nav.setSelectedItemId(R.id.nav_cart);

        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Về Trang chủ (Xóa các màn hình thừa đi cho nhẹ máy)
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;

            } else if (itemId == R.id.nav_category) {
                // Mở trang Danh mục
                startActivity(new Intent(CartActivity.this, AllCategoriesActivity.class));
                finish();
                return true;

            } else if (itemId == R.id.nav_cart) {
                // Đang ở trang Giỏ hàng rồi thì không làm gì cả
                return true;

            } else if (itemId == R.id.nav_wishlist) {
                // Mở trang Danh sách Yêu thích
                startActivity(new Intent(CartActivity.this, WishlistActivity.class));
                finish();
                return true;
            }
            else if (itemId == R.id.nav_account) {
                startActivity(new Intent(CartActivity.this, AccountActivity.class));
                return true;
            }
            return false;
        });
    }

    // PHẢI CÓ HÀM NÀY ĐỂ HẾT LỖI "this::updateTotal"
    private void updateTotal() {
        DecimalFormat df = new DecimalFormat("#,###");
        double total = CartManager.getInstance(this).getTotal();
        tvTotal.setText("₫ " + df.format(total) + " VND");
    }
}