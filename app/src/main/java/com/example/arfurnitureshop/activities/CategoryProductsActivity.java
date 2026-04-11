package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.ProductAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryProductsActivity extends AppCompatActivity {

    private RecyclerView rvCategoryProducts;
    private ProductAdapter productAdapter;

    // Khai báo 2 danh sách: 1 gốc, 1 để hiển thị sau khi lọc
    private List<Product> originalProductList = new ArrayList<>();
    private List<Product> filteredProductList = new ArrayList<>();

    private Spinner spinnerSortBy;
    private LinearLayout llFilter;

    // Các biến lưu trạng thái Bộ lọc
    private Double currentMinPrice = null;
    private Double currentMaxPrice = null;
    private int currentSortOption = 0;

    // Các lựa chọn sắp xếp y hệt AllProductsActivity
    private final String[] sortOptionsArray = {
            "Mới nhất",
            "Giá: Thấp đến Cao",
            "Giá: Cao đến Thấp",
            "Cũ nhất"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        rvCategoryProducts = findViewById(R.id.rvAllProducts);
        rvCategoryProducts.setLayoutManager(new GridLayoutManager(this, 2));

        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        llFilter = findViewById(R.id.llFilter);

        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");

        // ==========================================
        // ÁNH XẠ VÀ CÀI ĐẶT HEADER
        // ==========================================
        View headerView = findViewById(R.id.headerAllProducts);
        if (headerView != null) {
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText(categoryName != null ? categoryName : "Sản phẩm theo danh mục");
            }

            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) btnBack.setOnClickListener(v -> finish());

            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(CategoryProductsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        // ==========================================
        // CÀI ĐẶT SPINNER SẮP XẾP
        // ==========================================
        ArrayAdapter<String> adapterSortBy = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptionsArray);
        adapterSortBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(adapterSortBy);

        spinnerSortBy.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                currentSortOption = position;
                // Nếu danh sách gốc chưa tải xong thì không làm gì, tải xong thì tự động sắp xếp lại
                if (!originalProductList.isEmpty()) {
                    applyFilterAndSort();
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Bắt sự kiện Lọc giá
        llFilter.setOnClickListener(v -> showPriceFilterDialog());

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
                            // Lưu vào danh sách gốc
                            originalProductList = response.body();
                            // Tiến hành phân loại lần đầu
                            applyFilterAndSort();
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

    // ==========================================
    // HÀM LỌC VÀ SẮP XẾP NỘI BỘ
    // ==========================================
    private void applyFilterAndSort() {
        if (originalProductList == null || originalProductList.isEmpty()) return;

        filteredProductList.clear();

        // 1. Bước LỌC GIÁ
        for (Product p : originalProductList) {
            double finalPrice = p.getFinalPrice(); // Lấy giá sau khi giảm để chuẩn xác
            boolean isValid = true;

            if (currentMinPrice != null && finalPrice < currentMinPrice) isValid = false;
            if (currentMaxPrice != null && finalPrice > currentMaxPrice) isValid = false;

            if (isValid) {
                filteredProductList.add(p);
            }
        }

        // 2. Bước SẮP XẾP
        switch (currentSortOption) {
            case 0: // Mới nhất (Sắp xếp theo ID giảm dần)
                Collections.sort(filteredProductList, (p1, p2) -> Integer.compare(p2.getId(), p1.getId()));
                break;
            case 1: // Giá thấp đến cao
                Collections.sort(filteredProductList, (p1, p2) -> Double.compare(p1.getFinalPrice(), p2.getFinalPrice()));
                break;
            case 2: // Giá cao đến thấp
                Collections.sort(filteredProductList, (p1, p2) -> Double.compare(p2.getFinalPrice(), p1.getFinalPrice()));
                break;
            case 3: // Cũ nhất (Sắp xếp theo ID tăng dần)
                Collections.sort(filteredProductList, (p1, p2) -> Integer.compare(p1.getId(), p2.getId()));
                break;
        }

        // 3. Đẩy lên giao diện
        if (productAdapter == null) {
            productAdapter = new ProductAdapter(filteredProductList);
            rvCategoryProducts.setAdapter(productAdapter);
        } else {
            productAdapter.notifyDataSetChanged();
        }

        if (filteredProductList.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào trong khoảng giá này!", Toast.LENGTH_SHORT).show();
        }
    }

    // ==========================================
    // HIỂN THỊ HỘP THOẠI LỌC GIÁ (Giống AllProductsActivity)
    // ==========================================
    private void showPriceFilterDialog() {
        LinearLayout layoutDialog = new LinearLayout(this);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(48, 48, 48, 12);

        EditText edtMinPrice = new EditText(this);
        edtMinPrice.setHint("Giá tối thiểu (VD: 1000000)");
        edtMinPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (currentMinPrice != null) edtMinPrice.setText(String.valueOf(currentMinPrice.intValue()));
        layoutDialog.addView(edtMinPrice);

        EditText edtMaxPrice = new EditText(this);
        edtMaxPrice.setHint("Giá tối đa (VD: 5000000)");
        edtMaxPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (currentMaxPrice != null) edtMaxPrice.setText(String.valueOf(currentMaxPrice.intValue()));
        layoutDialog.addView(edtMaxPrice);

        new AlertDialog.Builder(this)
                .setTitle("Lọc theo giá")
                .setView(layoutDialog)
                .setPositiveButton("ÁP DỤNG", (dialog, which) -> {
                    String min = edtMinPrice.getText().toString().trim();
                    String max = edtMaxPrice.getText().toString().trim();
                    try {
                        currentMinPrice = min.isEmpty() ? null : Double.parseDouble(min);
                        currentMaxPrice = max.isEmpty() ? null : Double.parseDouble(max);
                        applyFilterAndSort(); // Bấm nút thì sẽ tiến hành lọc và xếp lại
                    } catch (NumberFormatException e) {
                        Toast.makeText(CategoryProductsActivity.this, "Vui lòng nhập số hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("XÓA LỌC", (dialog, which) -> {
                    currentMinPrice = null;
                    currentMaxPrice = null;
                    applyFilterAndSort(); // Reset list
                })
                .show();
    }
}