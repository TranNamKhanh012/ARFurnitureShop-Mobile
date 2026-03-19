package com.example.arfurnitureshop.activities;

import android.os.Bundle;
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

    // Biến lưu trạng thái lọc
    private String currentKeyword = "";
    private Double currentMinPrice = null;
    private Double currentMaxPrice = null;
    private String currentSortBy = "date_desc"; // Mặc định: Mới nhất

    // Dữ liệu cho Dropdown
    private final String[] sortOptionsArray = {"Relevance (Rating)", "Price: Low to High", "Price: High to Low", "Newest", "Oldest"};
    private final Map<String, String> sortByValueMap = new HashMap<String, String>() {{
        put("Relevance (Rating)", "rating_desc");
        put("Price: Low to High", "price_asc");
        put("Price: High to Low", "price_desc");
        put("Newest", "date_desc");
        put("Oldest", "date_asc");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results); // File giao diện XML hôm trước

        rvFilteredProducts = findViewById(R.id.rvFilteredProducts);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        llFilter = findViewById(R.id.llFilter);
        ImageView ivBack = findViewById(R.id.ivBack);

        // Hiển thị dạng lưới 2 cột
        rvFilteredProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // Nhận từ khóa từ Trang chủ truyền sang
        currentKeyword = getIntent().getStringExtra("SEARCH_KEYWORD");
        if (currentKeyword == null) currentKeyword = "";
        tvResultsCount.setText("Search results for: \"" + currentKeyword + "\"");

        // Cài đặt Dropdown Sắp xếp
        ArrayAdapter<String> adapterSortBy = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptionsArray);
        adapterSortBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(adapterSortBy);

        spinnerSortBy.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                currentSortBy = sortByValueMap.get(sortOptionsArray[position]);
                fetchFilteredSortedProducts(); // Gọi API ngay khi đổi kiểu sắp xếp
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Bắt sự kiện mở hộp thoại Lọc Giá
        llFilter.setOnClickListener(v -> showPriceFilterDialog());

        ivBack.setOnClickListener(v -> finish());

        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

    // ==========================================
    // GỌI API ĐỂ LẤY KẾT QUẢ
    // ==========================================
    private void fetchFilteredSortedProducts() {
        apiService.getFilteredSortedProducts(currentKeyword, currentMinPrice, currentMaxPrice, currentSortBy)
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> products = response.body();

                            // Cập nhật dòng chữ số lượng kết quả
                            tvResultsCount.setText("Found " + products.size() + " results for: \"" + currentKeyword + "\"");

                            // Đổ dữ liệu vào Adapter
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

    // ==========================================
    // HỘP THOẠI NHẬP KHOẢNG GIÁ (DIALOG)
    // ==========================================
    private void showPriceFilterDialog() {
        LinearLayout layoutDialog = new LinearLayout(this);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setPadding(48, 48, 48, 12);

        EditText edtMinPrice = new EditText(this);
        edtMinPrice.setHint("Min Price (VD: 1000000)");
        edtMinPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (currentMinPrice != null) edtMinPrice.setText(String.valueOf(currentMinPrice.intValue()));
        layoutDialog.addView(edtMinPrice);

        EditText edtMaxPrice = new EditText(this);
        edtMaxPrice.setHint("Max Price (VD: 5000000)");
        edtMaxPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (currentMaxPrice != null) edtMaxPrice.setText(String.valueOf(currentMaxPrice.intValue()));
        layoutDialog.addView(edtMaxPrice);

        new AlertDialog.Builder(this)
                .setTitle("Filter by Price")
                .setView(layoutDialog)
                .setPositiveButton("APPLY", (dialog, which) -> {
                    String min = edtMinPrice.getText().toString().trim();
                    String max = edtMaxPrice.getText().toString().trim();
                    try {
                        currentMinPrice = min.isEmpty() ? null : Double.parseDouble(min);
                        currentMaxPrice = max.isEmpty() ? null : Double.parseDouble(max);
                        fetchFilteredSortedProducts(); // Lọc xong thì gọi lại API
                    } catch (NumberFormatException e) {
                        Toast.makeText(SearchResultsActivity.this, "Vui lòng nhập số hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CLEAR", (dialog, which) -> {
                    currentMinPrice = null;
                    currentMaxPrice = null;
                    fetchFilteredSortedProducts();
                })
                .show();
    }
}