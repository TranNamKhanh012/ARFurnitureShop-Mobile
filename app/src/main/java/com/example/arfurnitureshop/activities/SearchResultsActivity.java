package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView rvFilteredProducts;
    private ProductAdapter productAdapter;
    private ApiService apiService;
    private TextView tvResultsCount;
    private Spinner spinnerSortBy;
    private LinearLayout llFilter;

    private String currentKeyword = "";
    private Double currentMinPrice = null;
    private Double currentMaxPrice = null;
    private String currentSortBy = "date_desc";

    private final String[] sortOptionsArray = {
            "Mới nhất",
            "Giá: Thấp đến Cao",
            "Giá: Cao đến Thấp",
            "Cũ nhất"
    };

    private final Map<String, String> sortByValueMap = new HashMap<String, String>() {{
        put("Mới nhất", "rating_desc");
        put("Giá: Thấp đến Cao", "price_asc");
        put("Giá: Cao đến Thấp", "price_desc");
        put("Cũ nhất", "date_asc");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        rvFilteredProducts = findViewById(R.id.rvFilteredProducts);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        llFilter = findViewById(R.id.llFilter);

        // Hiển thị dạng lưới 2 cột
        rvFilteredProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // Nhận từ khóa
        currentKeyword = getIntent().getStringExtra("SEARCH_KEYWORD");
        if (currentKeyword == null) currentKeyword = "";

        tvResultsCount.setText("Kết quả tìm kiếm cho: \"" + currentKeyword + "\"");

        // ==========================================
        // ĐÃ ĐỒNG BỘ: ÁNH XẠ HEADER DÙNG CHUNG
        // ==========================================
        View headerView = findViewById(R.id.headerSearch);
        if (headerView != null) {
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText("Kết quả tìm kiếm"); // Đổi tiêu đề tiếng Việt
            }

            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // Gắn sự kiện về trang chủ nếu header có nút Home
            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }
        // ==========================================

        // Cài đặt Dropdown Sắp xếp
        ArrayAdapter<String> adapterSortBy = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptionsArray);
        adapterSortBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(adapterSortBy);

        spinnerSortBy.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                currentSortBy = sortByValueMap.get(sortOptionsArray[position]);
                fetchFilteredSortedProducts();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Bắt sự kiện mở hộp thoại Lọc Giá
        llFilter.setOnClickListener(v -> showPriceFilterDialog());

        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    private void fetchFilteredSortedProducts() {
        apiService.getFilteredSortedProducts(currentKeyword, currentMinPrice, currentMaxPrice, currentSortBy)
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> products = response.body();

                            tvResultsCount.setText("Tìm thấy " + products.size() + " kết quả cho: \"" + currentKeyword + "\"");

                            productAdapter = new ProductAdapter(products);
                            rvFilteredProducts.setAdapter(productAdapter);
                        } else {
                            Toast.makeText(SearchResultsActivity.this, "Không tìm thấy dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Toast.makeText(SearchResultsActivity.this, "Lỗi kết nối máy chủ!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

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
                        fetchFilteredSortedProducts();
                    } catch (NumberFormatException e) {
                        Toast.makeText(SearchResultsActivity.this, "Vui lòng nhập số hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("XÓA LỌC", (dialog, which) -> {
                    currentMinPrice = null;
                    currentMaxPrice = null;
                    fetchFilteredSortedProducts();
                })
                .show();
    }
}