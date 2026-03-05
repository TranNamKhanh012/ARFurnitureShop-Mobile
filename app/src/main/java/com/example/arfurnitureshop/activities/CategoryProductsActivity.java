package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.util.Log;
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
    private TextView tvCategoryName;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tái sử dụng luôn giao diện của AllProducts cho tiện, vì cấu trúc y hệt nhau
        setContentView(R.layout.activity_all_products);

        rvCategoryProducts = findViewById(R.id.rvAllProductsVertical);
        tvCategoryName = findViewById(R.id.tvPageTitle);
        ivBack = findViewById(R.id.ivBack);

        // Hiển thị lưới 2 cột
        rvCategoryProducts.setLayoutManager(new GridLayoutManager(this, 2));

        ivBack.setOnClickListener(v -> finish());

        // Nhận dữ liệu từ CategoryAdapter truyền sang
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        if (categoryName != null) {
            tvCategoryName.setText(categoryName);
        }

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