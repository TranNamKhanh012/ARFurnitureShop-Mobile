package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.CategoryAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.Category;
import com.example.arfurnitureshop.utils.MenuHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllCategoriesActivity extends AppCompatActivity {
    private RecyclerView rvAllCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);

        rvAllCategories = findViewById(R.id.rvAllProductsVertical);
        // Hiển thị dạng lưới 2 cột cho đẹp
        rvAllCategories.setLayoutManager(new GridLayoutManager(this, 2));

        fetchCategories();

        // ==========================================
        // GỌI TRỢ LÝ MENU RA LÀM VIỆC (CHỈ 3 DÒNG CODE)
        // ==========================================
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        ImageView ivMenu = findViewById(R.id.ivMenu);

        // Giao toàn bộ việc đóng/mở menu cho MenuHelper xử lý
        MenuHelper.setupMenu(this, drawerLayout, ivMenu, navigationView);


        // Ánh xạ nút Back và cài đặt sự kiện Click
        // ==========================================
        // THANH ĐIỀU HƯỚNG DƯỚI CÙNG (CHUYỂN TRANG SIÊU MƯỢT, KHÔNG CHỚP NHÁY)
        // ==========================================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // [QUAN TRỌNG] TÙY CHỈNH CHO TỪNG TRANG:
        // Ở trang nào thì bạn đổi ID thành icon của trang đó nhé!
        // (Ví dụ: Trang chủ -> R.id.nav_home | Giỏ hàng -> R.id.nav_cart | Tài khoản -> R.id.nav_account)
        bottomNav.setSelectedItemId(R.id.nav_category);

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

    private void fetchCategories() {
        RetrofitClient.getClient().create(ApiService.class).getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryAdapter adapter = new CategoryAdapter(response.body());
                    rvAllCategories.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AllCategoriesActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}