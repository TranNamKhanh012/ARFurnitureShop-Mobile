package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.ProductAdapter;
import com.example.arfurnitureshop.models.WishlistManager;
import com.example.arfurnitureshop.utils.MenuHelper; // <--- Import Trợ lý
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView rvWishlist;
    private TextView tvEmptyWishlist;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // 1. Ánh xạ View
        rvWishlist = findViewById(R.id.rvWishlist);
        tvEmptyWishlist = findViewById(R.id.tvEmptyWishlist);

        // 2. Thiết lập RecyclerView hiển thị 2 cột
        rvWishlist.setLayoutManager(new GridLayoutManager(this, 2));

        // Truyền thẳng danh sách từ WishlistManager vào Adapter
        productAdapter = new ProductAdapter(WishlistManager.wishlistProducts);
        rvWishlist.setAdapter(productAdapter);

        // Kiểm tra nếu danh sách trống thì hiện thông báo
        checkEmptyList();

        // ==========================================
        // GỌI TRỢ LÝ MENU RA LÀM VIỆC (CHỈ 3 DÒNG CODE)
        // ==========================================
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        ImageView ivMenu = findViewById(R.id.ivMenu);

        // Giao toàn bộ việc đóng/mở menu cho MenuHelper xử lý
        MenuHelper.setupMenu(this, drawerLayout, ivMenu, navigationView);

        // ==========================================
        // THANH ĐIỀU HƯỚNG DƯỚI CÙNG
        // ==========================================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setSelectedItemId(R.id.nav_wishlist);

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
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
        checkEmptyList();
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        com.example.arfurnitureshop.utils.BadgeUtils.loadCachedBadges(this, bottomNav);
    }

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