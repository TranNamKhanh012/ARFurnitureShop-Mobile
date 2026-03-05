package com.example.arfurnitureshop.activities;

import android.os.Bundle;
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

public class AllProductsActivity extends AppCompatActivity {

    private RecyclerView rvAllProductsVertical;
    private TextView tvPageTitle;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        rvAllProductsVertical = findViewById(R.id.rvAllProductsVertical);
        tvPageTitle = findViewById(R.id.tvPageTitle);
        ivBack = findViewById(R.id.ivBack);

        // Lấy tiêu đề được truyền từ MainActivity (Best Sellers hoặc All Products)
        String title = getIntent().getStringExtra("PAGE_TITLE");
        if (title != null) {
            tvPageTitle.setText(title);
        }

        // Cài đặt RecyclerView hiển thị dạng lưới (Grid) 2 cột giống Shopee
        // Nếu muốn hiển thị danh sách 1 cột kéo dài, đổi thành LinearLayoutManager(this)
        rvAllProductsVertical.setLayoutManager(new GridLayoutManager(this, 2));

        // Nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // Gọi API lấy dữ liệu
        fetchProducts();
    }

    private void fetchProducts() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Product>> call = apiService.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Tái sử dụng lại ProductAdapter cũ, nó sẽ tự động đổ dữ liệu vào Grid
                    ProductAdapter adapter = new ProductAdapter(response.body());
                    rvAllProductsVertical.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(AllProductsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}