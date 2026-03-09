package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.ProductAdapter;
import com.example.arfurnitureshop.models.WishlistManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView rvWishlist;
    private TextView tvEmptyWishlist;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // 1. Ánh xạ View
        ImageView ivBack = findViewById(R.id.ivBack);
        rvWishlist = findViewById(R.id.rvWishlist);
        tvEmptyWishlist = findViewById(R.id.tvEmptyWishlist);

        // 2. Xử lý Nút Back
        ivBack.setOnClickListener(v -> finish());

        // 3. Thiết lập RecyclerView hiển thị 2 cột
        rvWishlist.setLayoutManager(new GridLayoutManager(this, 2));

        // Truyền thẳng danh sách từ WishlistManager vào Adapter
        productAdapter = new ProductAdapter(WishlistManager.wishlistProducts);
        rvWishlist.setAdapter(productAdapter);

        // Kiểm tra nếu danh sách trống thì hiện thông báo
        checkEmptyList();

        // ==========================================
        // 4. XỬ LÝ THANH MENU FOOTER (BOTTOM NAVIGATION)
        // ==========================================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Bật sáng icon Wishlist (Trái tim) để báo cho user biết đang ở trang này
        bottomNav.setSelectedItemId(R.id.nav_wishlist);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Về Trang chủ (Dùng cờ để dọn dẹp các màn hình đè lên nhau)
                Intent intent = new Intent(WishlistActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;

            } else if (itemId == R.id.nav_category) {
                // Sang trang Danh mục
                startActivity(new Intent(WishlistActivity.this, AllCategoriesActivity.class));
                finish(); // Đóng trang hiện tại cho nhẹ máy
                return true;

            } else if (itemId == R.id.nav_cart) {
                // Sang trang Giỏ hàng
                startActivity(new Intent(WishlistActivity.this, CartActivity.class));
                finish();
                return true;

            }
            else if (itemId == R.id.nav_account) {
                startActivity(new Intent(WishlistActivity.this, AccountActivity.class));
                return true;
            }else if (itemId == R.id.nav_wishlist) {
                // Đang ở trang Wishlist rồi thì không làm gì cả
                return true;
            }
            return false;
        });
    }

    // Hàm này chạy mỗi khi màn hình Wishlist được mở lại (Resume)
    @Override
    protected void onResume() {
        super.onResume();
        // Báo cho Adapter biết dữ liệu có thể đã thay đổi (nếu user vừa thả/bỏ tim ở màn hình khác)
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
        checkEmptyList();
    }

    // Hàm kiểm tra ẩn/hiện danh sách
    private void checkEmptyList() {
        if (WishlistManager.wishlistProducts.isEmpty()) {
            rvWishlist.setVisibility(View.GONE);
            tvEmptyWishlist.setVisibility(View.VISIBLE);
        } else {
            rvWishlist.setVisibility(View.VISIBLE);
            tvEmptyWishlist.setVisibility(View.GONE);
        }
    }
}