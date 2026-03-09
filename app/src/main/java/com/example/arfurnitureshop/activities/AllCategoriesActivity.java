package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.CategoryAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.Category;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        rvAllCategories = findViewById(R.id.rvAllCategories);
        // Hiển thị dạng lưới 2 cột cho đẹp
        rvAllCategories.setLayoutManager(new GridLayoutManager(this, 2));

        fetchCategories();

        // Ánh xạ nút Back và cài đặt sự kiện Click
        android.widget.ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish()); // Đóng màn hình hiện tại

        // ==========================================
        // XỬ LÝ THANH ĐIỀU HƯỚNG DƯỚI CÙNG (BOTTOM NAVIGATION)
        // ==========================================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // 1. Chỉnh cho icon Danh mục sáng lên vì ta đang ở trang này
        bottomNav.setSelectedItemId(R.id.nav_category);

        // 2. Bắt sự kiện chuyển trang
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Về trang chủ
                Intent intent = new Intent(AllCategoriesActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_category) {
                // Đang ở Danh mục rồi thì đứng im
                return true;
            }
            else if (itemId == R.id.nav_cart) {
                // Sang Giỏ hàng
                Intent intent = new Intent(AllCategoriesActivity.this, CartActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (itemId == R.id.nav_wishlist) {
                // Mở trang Danh sách Yêu thích
                startActivity(new android.content.Intent(AllCategoriesActivity.this, com.example.arfurnitureshop.activities.WishlistActivity.class));
                return true;
            }
            else if (itemId == R.id.nav_account) {
                // Sang trang Tài khoản
                Intent intent = new Intent(AllCategoriesActivity.this, AccountActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
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