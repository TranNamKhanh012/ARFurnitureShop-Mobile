package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.ProductAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvBestSellers, rvAllProducts;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. ÁNH XẠ TOÀN BỘ VIEW TRƯỚC TIÊN ---
        rvBestSellers = findViewById(R.id.rvBestSellers);
        rvAllProducts = findViewById(R.id.rvAllProducts);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        TextView tvSeeAllBestSellers = findViewById(R.id.tvSeeAllBestSellers);
        TextView tvSeeAllProducts = findViewById(R.id.tvSeeAllProducts);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        NavigationView navigationView = findViewById(R.id.navigationView);

        // --- 2. CẤU HÌNH RECYCLERVIEW ---
        // Bắt buộc phải cấu hình LayoutManager trước khi gọi API
        if (rvBestSellers != null && rvAllProducts != null) {
            rvBestSellers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            rvAllProducts.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        }

        // --- 3. KHỞI TẠO MENU TRƯỢT (SIDEBAR) ---
        if (ivMenu != null && drawerLayout != null) {
            ivMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_profile) {
                    Toast.makeText(MainActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_setting) {
                    Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }

        // --- 4. XỬ LÝ NÚT "SEE ALL" ---
        if (tvSeeAllBestSellers != null) {
            tvSeeAllBestSellers.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AllProductsActivity.class);
                intent.putExtra("PAGE_TITLE", "Best Sellers");
                startActivity(intent);
            });
        }

        if (tvSeeAllProducts != null) {
            tvSeeAllProducts.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AllProductsActivity.class);
                intent.putExtra("PAGE_TITLE", "All Products");
                startActivity(intent);
            });
        }

        // --- 5. KHỞI TẠO BANNER CHẠY NGANG (VIEWPAGER2) ---
        androidx.viewpager2.widget.ViewPager2 viewPagerBanner = findViewById(R.id.viewPagerBanner);
        if (viewPagerBanner != null) {
            java.util.List<String> bannerImages = new java.util.ArrayList<>();
            bannerImages.add("https://images.unsplash.com/photo-1618220179428-22790b461013?q=80&w=1000&auto=format&fit=crop");
            bannerImages.add("https://images.unsplash.com/photo-1555041469-a586c61ea9bc?q=80&w=1000&auto=format&fit=crop");
            bannerImages.add("https://images.unsplash.com/photo-1586023492125-27b2c045efd7?q=80&w=1000&auto=format&fit=crop");

            com.example.arfurnitureshop.adapters.BannerAdapter bannerAdapter = new com.example.arfurnitureshop.adapters.BannerAdapter(bannerImages);
            viewPagerBanner.setAdapter(bannerAdapter);
        }

        // --- 6. XỬ LÝ THANH ĐIỀU HƯỚNG DƯỚI CÙNG (BOTTOM NAV) ---
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_category) {
                    Intent intent = new Intent(MainActivity.this, AllCategoriesActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                    return true;
                } else if (itemId == R.id.nav_wishlist) {
                    // Mở trang Danh sách Yêu thích
                    startActivity(new android.content.Intent(MainActivity.this, com.example.arfurnitureshop.activities.WishlistActivity.class));
                    return true;
                } else if (itemId == R.id.nav_account) {
                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                    return true;
                }
                return false;
            });
        }

        // --- 7. GỌI API NẠP DỮ LIỆU ---
        // Lệnh này phải để sau cùng, khi tất cả View đã được ánh xạ xong
        fetchProductsFromApi();
    }

    // ================= HÀM LẤY DỮ LIỆU TỪ BACKEND =================
    private void fetchProductsFromApi() {
        RetrofitClient.getClient().create(ApiService.class).getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fullList = response.body();

                    // Cắt 6 sản phẩm (Bọc thêm new ArrayList)
                    int limit = Math.min(fullList.size(), 6);
                    List<Product> limitedList = new java.util.ArrayList<>(fullList.subList(0, limit));

                    // Nạp dữ liệu vào (Đã check null để tránh lỗi)
                    productAdapter = new ProductAdapter(limitedList);
                    if (rvBestSellers != null) rvBestSellers.setAdapter(productAdapter);
                    if (rvAllProducts != null) rvAllProducts.setAdapter(productAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi lấy sản phẩm: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Cứ mỗi khi màn hình Home hiện lên lại, bắt buộc nó phải làm sáng nút Home
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
}