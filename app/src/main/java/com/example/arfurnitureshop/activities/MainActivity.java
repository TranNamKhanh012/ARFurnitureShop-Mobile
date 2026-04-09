package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.BannerAdapter;
import com.example.arfurnitureshop.adapters.ProductAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.CartItem;
import com.example.arfurnitureshop.models.Product;
import com.example.arfurnitureshop.models.WishlistManager;
import com.example.arfurnitureshop.utils.CartManager;
import com.example.arfurnitureshop.utils.MenuHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.badge.BadgeDrawable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvBestSellers, rvAllProducts;
    private ProductAdapter productAdapter;
    private ApiService apiService; // Khai báo dùng chung

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kiểm tra và áp dụng Dark Mode ngay khi vừa mở app
        android.content.SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false);
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        // Khởi tạo Retrofit
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // --- 1. ÁNH XẠ TOÀN BỘ VIEW TRƯỚC TIÊN ---
        rvBestSellers = findViewById(R.id.rvBestSellers);
        rvAllProducts = findViewById(R.id.rvAllProducts);
        TextView tvSeeAllBestSellers = findViewById(R.id.tvSeeAllBestSellers);
        TextView tvSeeAllProducts = findViewById(R.id.tvSeeAllProducts);

        // --- 2. CẤU HÌNH RECYCLERVIEW ---
        if (rvBestSellers != null && rvAllProducts != null) {
            rvBestSellers.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            rvAllProducts.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        }

        // --- 3. KHỞI TẠO MENU TRƯỢT (SIDEBAR) BẰNG MENUHELPER ---
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        NavigationView navigationView = findViewById(R.id.navigationView);

        if (drawerLayout != null && navigationView != null && ivMenu != null) {
            MenuHelper.setupMenu(this, drawerLayout, ivMenu, navigationView);
        }


        // --- 4. XỬ LÝ NÚT "SEE ALL" ---
        if (tvSeeAllBestSellers != null) {
            tvSeeAllBestSellers.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AllProductsActivity.class);
                intent.putExtra("PAGE_TITLE", "Sản phẩm giảm giá");
                intent.putExtra("SHOW_ONLY_DISCOUNT", true); // Báo hiệu lọc hàng giảm giá
                startActivity(intent);
            });
        }

        if (tvSeeAllProducts != null) {
            tvSeeAllProducts.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AllProductsActivity.class);
                intent.putExtra("PAGE_TITLE", "Tất cả sản phẩm");
                startActivity(intent);
            });
        }

        // --- 5. KHỞI TẠO BANNER CHẠY NGANG (VIEWPAGER2) ---
        ViewPager2 viewPagerBanner = findViewById(R.id.viewPagerBanner);
        if (viewPagerBanner != null) {
            List<String> bannerImages = new ArrayList<>();
            bannerImages.add("https://images.unsplash.com/photo-1511499767150-a48a237f0083?q=80&w=1000&auto=format&fit=crop");
            bannerImages.add("https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=1000&auto=format&fit=crop");
            bannerImages.add("https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=1000&auto=format&fit=crop");

            BannerAdapter bannerAdapter = new BannerAdapter(bannerImages);
            viewPagerBanner.setAdapter(bannerAdapter);
        }

        // --- 6. XỬ LÝ THANH ĐIỀU HƯỚNG DƯỚI CÙNG (BOTTOM NAV) ---
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == bottomNav.getSelectedItemId()) return true;

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
        }

        // --- 7. GỌI API NẠP DỮ LIỆU ---
        fetchProductsFromApi();

        // --- 8. XỬ LÝ THANH TÌM KIẾM MÀN HÌNH NỔI ---
        // --- 8. GỌI TRỢ LÝ TÌM KIẾM RA LÀM VIỆC ---
        com.example.arfurnitureshop.utils.SearchHelper.setupSearch(this);
        com.example.arfurnitureshop.utils.BadgeUtils.fetchAndCacheBadges(this);

        // GỌI TRỢ LÝ THÔNG BÁO RA LÀM VIỆC
        com.example.arfurnitureshop.utils.NotificationHelper.setupNotificationBell(this);
        // =========================================================
        // THÊM SỰ KIỆN NÚT AI NỔI TRÊN MÀN HÌNH CHÍNH
        // =========================================================
        com.google.android.material.floatingactionbutton.FloatingActionButton fabChatAi = findViewById(R.id.fabChatAi);
        if (fabChatAi != null) {
            fabChatAi.setOnClickListener(v -> {
                // Khi bấm vào sẽ mở trang Chat với AI
                android.content.Intent intent = new android.content.Intent(MainActivity.this, ChatAiActivity.class);
                startActivity(intent);
            });
        }
    }

    // ================= HÀM LẤY DỮ LIỆU TỪ BACKEND =================
    private void fetchProductsFromApi() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fullList = response.body();

                    // 1. Lọc Best Sellers (Có giảm giá)
                    List<Product> discountedProducts = new ArrayList<>();
                    for (Product p : fullList) {
                        if (p.getDiscount() > 0) discountedProducts.add(p);
                    }
                    int limitDiscount = Math.min(discountedProducts.size(), 6);
                    List<Product> finalBestSellers = new ArrayList<>(discountedProducts.subList(0, limitDiscount));

                    ProductAdapter bestSellersAdapter = new ProductAdapter(finalBestSellers);
                    if (rvBestSellers != null) rvBestSellers.setAdapter(bestSellersAdapter);

                    // 2. Lọc All Products
                    int limitAll = Math.min(fullList.size(), 6);
                    List<Product> finalAllProducts = new ArrayList<>(fullList.subList(0, limitAll));

                    productAdapter = new ProductAdapter(finalAllProducts);
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

    // ================= TỰ ĐỘNG ĐỒNG BỘ DỮ LIỆU =================
    @Override
    protected void onResume() {
        super.onResume();
        syncDataFromServer();
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        com.example.arfurnitureshop.utils.BadgeUtils.loadCachedBadges(this, bottomNav);
        // ĐỒNG BỘ SỐ CHUÔNG THÔNG BÁO
        com.example.arfurnitureshop.utils.NotificationHelper.checkPendingReviews(this);
    }


    private void syncDataFromServer() {
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", -1);

        if (userId != -1) {
            // Đồng bộ Wishlist
            apiService.getWishlist(userId).enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        WishlistManager.clear();
                        WishlistManager.wishlistProducts.addAll(response.body());
                    }
                }
                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {}
            });

            // Đồng bộ Giỏ hàng
            apiService.getCart(userId).enqueue(new Callback<List<CartItem>>() {
                @Override
                public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CartManager.getInstance(MainActivity.this).clear();
                        for (CartItem item : response.body()) {
                            CartManager.getInstance(MainActivity.this).add(item);
                        }
                    }
                }
                @Override
                public void onFailure(Call<List<CartItem>> call, Throwable t) {}
            });
        } else {
            CartManager.getInstance(this).clear();
            WishlistManager.clear();
        }
    }
}