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

        // --- XỬ LÝ THANH MENU FOOTER ---
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // Làm sáng icon Danh mục
        bottomNav.setSelectedItemId(R.id.nav_category);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Về Trang chủ
                android.content.Intent intent = new android.content.Intent(AllCategoriesActivity.this, MainActivity.class);
                intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_category) {
                return true; // Đang ở trang danh mục nên không làm gì cả
            } else if (itemId == R.id.nav_cart) {
                // Sang Giỏ hàng
                startActivity(new android.content.Intent(AllCategoriesActivity.this, CartActivity.class));
                return true;
            }
            return false;
        });
        // Ánh xạ nút Back và cài đặt sự kiện Click
        android.widget.ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> {
            finish(); // Lệnh finish() sẽ đóng màn hình hiện tại và tự động trượt về màn hình trước đó
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