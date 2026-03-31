package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.ProductAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryProductsActivity extends AppCompatActivity {

    private RecyclerView rvCategoryProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tái sử dụng luôn giao diện của AllProducts cho tiện, vì cấu trúc y hệt nhau
        setContentView(R.layout.activity_all_products);

        rvCategoryProducts = findViewById(R.id.rvAllProducts);
        rvCategoryProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // Nhận dữ liệu từ CategoryAdapter truyền sang
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        // ==========================================
        // ÁNH XẠ VÀ CÀI ĐẶT HEADER DÙNG CHUNG
        // (Tìm qua id headerAllProducts vì dùng chung layout activity_all_products)
        // ==========================================
        View headerView = findViewById(R.id.headerAllProducts);
        if (headerView != null) {

            // 1. Gán tiêu đề động bằng tên danh mục
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText(categoryName != null ? categoryName : "Sản phẩm theo danh mục");
            }

            // 2. Nút Back
            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // 3. Nút Home
            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(CategoryProductsActivity.this, MainActivity.class);
                    // Dọn dẹp RAM, quay thẳng về màn hình chính
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        // Gọi API nạp dữ liệu
        if (categoryId != -1) {
            fetchProductsByCategory(categoryId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID danh mục", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchProductsByCategory(int categoryId) {
        RetrofitClient.getClient().create(ApiService.class).getProductsByCategory(categoryId)
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> products = response.body();
                            ProductAdapter adapter = new ProductAdapter(products);
                            rvCategoryProducts.setAdapter(adapter);
                        } else {
                            Toast.makeText(CategoryProductsActivity.this, "Danh mục này chưa có sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Log.e("API_ERROR", "Lỗi: " + t.getMessage());
                        Toast.makeText(CategoryProductsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}