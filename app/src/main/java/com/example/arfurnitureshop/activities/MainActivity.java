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
        // (Đã xóa dòng khai báo bottomNav thừa ở đây)

        TextView tvSeeAllBestSellers = findViewById(R.id.tvSeeAllBestSellers);
        TextView tvSeeAllProducts = findViewById(R.id.tvSeeAllProducts);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView ivMenu = findViewById(R.id.ivMenu);
        NavigationView navigationView = findViewById(R.id.navigationView);

        // --- 2. CẤU HÌNH RECYCLERVIEW ---
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
                    // Mở màn hình Profile khi bấm vào "My Profile"
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
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

                // NÉM THÊM 1 CÁI CỜ BÁO HIỆU SANG TRANG BÊN KIA:
                intent.putExtra("SHOW_ONLY_DISCOUNT", true);

                startActivity(intent);
            });
        }

        // Nút See All của All Products thì giữ nguyên (không lọc)
        if (tvSeeAllProducts != null) {
            tvSeeAllProducts.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AllProductsActivity.class);
                intent.putExtra("PAGE_TITLE", "All Products");
                // Không truyền cờ lọc vào đây
                startActivity(intent);
            });
        }

        // --- 5. KHỞI TẠO BANNER CHẠY NGANG (VIEWPAGER2) ---
        androidx.viewpager2.widget.ViewPager2 viewPagerBanner = findViewById(R.id.viewPagerBanner);
        if (viewPagerBanner != null) {
            java.util.List<String> bannerImages = new java.util.ArrayList<>();
            bannerImages.add("https://images.unsplash.com/photo-1511499767150-a48a237f0083?q=80&w=1000&auto=format&fit=crop");
            bannerImages.add("https://images.unsplash.com/photo-1523275335684-37898b6baf30?q=80&w=1000&auto=format&fit=crop");
            bannerImages.add("https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=1000&auto=format&fit=crop");

            com.example.arfurnitureshop.adapters.BannerAdapter bannerAdapter = new com.example.arfurnitureshop.adapters.BannerAdapter(bannerImages);
            viewPagerBanner.setAdapter(bannerAdapter);
        }

        // --- 6. XỬ LÝ THANH ĐIỀU HƯỚNG DƯỚI CÙNG (BOTTOM NAV) ---
        // ==========================================
        // THANH ĐIỀU HƯỚNG DƯỚI CÙNG (CHUYỂN TRANG SIÊU MƯỢT, KHÔNG CHỚP NHÁY)
        // ==========================================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_home);

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

        // --- 7. GỌI API NẠP DỮ LIỆU ---
        fetchProductsFromApi();
    }

    // ================= HÀM LẤY DỮ LIỆU TỪ BACKEND =================
    // ================= HÀM LẤY DỮ LIỆU TỪ BACKEND =================
    private void fetchProductsFromApi() {
        RetrofitClient.getClient().create(ApiService.class).getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fullList = response.body();

                    // ----------------------------------------------------
                    // 1. TẠO DANH SÁCH RIÊNG CHO BEST SELLERS (CHỈ LẤY HÀNG GIẢM GIÁ)
                    // ----------------------------------------------------
                    List<Product> discountedProducts = new java.util.ArrayList<>();
                    for (Product p : fullList) {
                        if (p.getDiscount() > 0) {
                            discountedProducts.add(p);
                        }
                    }

                    // Cắt lấy tối đa 6 sản phẩm giảm giá
                    int limitDiscount = Math.min(discountedProducts.size(), 6);
                    List<Product> finalBestSellers = new java.util.ArrayList<>(discountedProducts.subList(0, limitDiscount));

                    // Nạp vào thanh trượt Best Sellers
                    ProductAdapter bestSellersAdapter = new ProductAdapter(finalBestSellers);
                    if (rvBestSellers != null) rvBestSellers.setAdapter(bestSellersAdapter);

                    // ----------------------------------------------------
                    // 2. TẠO DANH SÁCH CHO ALL PRODUCTS (Lấy tất cả bình thường)
                    // ----------------------------------------------------
                    // Cắt lấy tối đa 6 sản phẩm mới nhất
                    int limitAll = Math.min(fullList.size(), 6);
                    List<Product> finalAllProducts = new java.util.ArrayList<>(fullList.subList(0, limitAll));

                    // Nạp vào thanh trượt All Products
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

    // =========================================================
    // 1. TỰ ĐỘNG KÉO DỮ LIỆU MỖI KHI MỞ TRANG CHỦ LÊN
    // =========================================================
    @Override
    protected void onResume() {
        super.onResume();

        // 1. Kéo dữ liệu ngầm từ C# về
        syncDataFromServer();

        // 2. CẬP NHẬT GIAO DIỆN TIM NGAY LẬP TỨC TỪ KHO LƯU TRỮ RAM
        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }

    // =========================================================
    // 2. HÀM ĐỒNG BỘ DỮ LIỆU TỪ SERVER C# VỀ ĐIỆN THOẠI
    // =========================================================
    private void syncDataFromServer() {
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", -1);

        if (userId != -1) {
            // 1. Kéo Wishlist từ C# về
            com.example.arfurnitureshop.api.ApiService.apiService.getWishlist(userId).enqueue(new retrofit2.Callback<java.util.List<com.example.arfurnitureshop.models.Product>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.List<com.example.arfurnitureshop.models.Product>> call, retrofit2.Response<java.util.List<com.example.arfurnitureshop.models.Product>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        com.example.arfurnitureshop.models.WishlistManager.clear();
                        com.example.arfurnitureshop.models.WishlistManager.wishlistProducts.addAll(response.body());
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<java.util.List<com.example.arfurnitureshop.models.Product>> call, Throwable t) {}
            });

            // 2. Kéo Giỏ hàng từ C# về
            com.example.arfurnitureshop.api.ApiService.apiService.getCart(userId).enqueue(new retrofit2.Callback<java.util.List<com.example.arfurnitureshop.models.CartItem>>() {
                @Override
                public void onResponse(retrofit2.Call<java.util.List<com.example.arfurnitureshop.models.CartItem>> call, retrofit2.Response<java.util.List<com.example.arfurnitureshop.models.CartItem>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Xóa sạch giỏ hàng cũ ở điện thoại
                        com.example.arfurnitureshop.utils.CartManager.getInstance(MainActivity.this).clear();

                        // Lặp qua từng món Server gửi về và lưu vào SQLite để hiển thị
                        for (com.example.arfurnitureshop.models.CartItem item : response.body()) {
                            com.example.arfurnitureshop.utils.CartManager.getInstance(MainActivity.this).add(item);
                        }
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<java.util.List<com.example.arfurnitureshop.models.CartItem>> call, Throwable t) {}
            });
        } else {
            // Nếu phát hiện chưa đăng nhập, tự động quét sạch kho nội bộ
            com.example.arfurnitureshop.utils.CartManager.getInstance(this).clear();
            com.example.arfurnitureshop.models.WishlistManager.clear();
        }
    }
}