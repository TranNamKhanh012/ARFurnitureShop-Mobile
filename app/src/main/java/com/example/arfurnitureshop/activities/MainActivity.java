package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.ProductAdapter;
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

        // --- 1. KHỞI TẠO MENU TRƯỢT (SIDEBAR) ---
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        NavigationView navigationView = findViewById(R.id.navigationView);

        // Mở menu trượt khi bấm vào nút 3 gạch ngang
        ivMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        // Xử lý sự kiện khi bấm vào các mục trong menu trượt
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                Toast.makeText(MainActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_setting) {
                Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
            }
            // Đóng menu sau khi bấm xong
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // --- 2. ÁNH XẠ VIEW GIAO DIỆN CHÍNH ---
        rvBestSellers = findViewById(R.id.rvBestSellers);
        rvAllProducts = findViewById(R.id.rvAllProducts);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Cấu hình lướt ngang cho các danh sách
        rvBestSellers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvAllProducts.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        // ========================================================
        // --- 2.5. KHỞI TẠO BANNER CHẠY NGANG (VIEWPAGER2) ---
        // ========================================================
        androidx.viewpager2.widget.ViewPager2 viewPagerBanner = findViewById(R.id.viewPagerBanner);

        java.util.List<String> bannerImages = new java.util.ArrayList<>();
        bannerImages.add("https://images.unsplash.com/photo-1618220179428-22790b461013?q=80&w=1000&auto=format&fit=crop");
        bannerImages.add("https://images.unsplash.com/photo-1555041469-a586c61ea9bc?q=80&w=1000&auto=format&fit=crop");
        bannerImages.add("https://images.unsplash.com/photo-1586023492125-27b2c045efd7?q=80&w=1000&auto=format&fit=crop");

        com.example.arfurnitureshop.adapters.BannerAdapter bannerAdapter = new com.example.arfurnitureshop.adapters.BannerAdapter(bannerImages);
        viewPagerBanner.setAdapter(bannerAdapter);
        // ========================================================

        // --- 3. GỌI API ĐỂ NẠP DỮ LIỆU THẬT ---
        fetchProductsFromApi();

        // --- 4. XỬ LÝ THANH ĐIỀU HƯỚNG DƯỚI CÙNG (BOTTOM NAV) ---
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Toast.makeText(MainActivity.this, "Đang ở Trang chủ", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_category) {
                Toast.makeText(MainActivity.this, "Mở Danh mục", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_cart) {
                // Chuyển sang trang Giỏ hàng
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_wishlist) {
                Toast.makeText(MainActivity.this, "Mở Wishlist", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_account) {
                Toast.makeText(MainActivity.this, "Mở Tài khoản", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    // ================= HÀM LẤY DỮ LIỆU TỪ BACKEND =================
    private void fetchProductsFromApi() {
        RetrofitClient.getApiService().getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    // Nạp dữ liệu thật vào Adapter và hiển thị lên RecyclerView
                    productAdapter = new ProductAdapter(products);
                    rvBestSellers.setAdapter(productAdapter);
                    rvAllProducts.setAdapter(productAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Không lấy được dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi lấy sản phẩm: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}