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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllProductsActivity extends AppCompatActivity {

    private RecyclerView rvAllProducts;
    private ProductAdapter productAdapter;
    private ApiService apiService;
    private Spinner spinnerSortBy;
    private LinearLayout llFilter;
    private TextView tvPageTitle;

    private Double currentMinPrice = null;
    private Double currentMaxPrice = null;
    private String currentSortBy = "date_desc";
    private boolean showOnlyDiscount = false;

    private final String[] sortOptionsArray = {"Newest", "Price: Low to High", "Price: High to Low", "Rating: High to Low", "Oldest"};
    private final Map<String, String> sortByValueMap = new HashMap<String, String>() {{
        put("Newest", "date_desc");
        put("Price: Low to High", "price_asc");
        put("Price: High to Low", "price_desc");
        put("Rating: High to Low", "rating_desc");
        put("Oldest", "date_asc");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        // ĐÃ SỬA: Khởi tạo API ngay trên cùng để tránh lỗi Null
        apiService = RetrofitClient.getClient().create(ApiService.class);

        rvAllProducts = findViewById(R.id.rvAllProducts);
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        llFilter = findViewById(R.id.llFilter);
        tvPageTitle = findViewById(R.id.tvPageTitle);
        ImageView ivBack = findViewById(R.id.ivBack);

        rvAllProducts.setLayoutManager(new GridLayoutManager(this, 2));

        String pageTitle = getIntent().getStringExtra("PAGE_TITLE");
        if (pageTitle != null) tvPageTitle.setText(pageTitle);
        showOnlyDiscount = getIntent().getBooleanExtra("SHOW_ONLY_DISCOUNT", false);

        ArrayAdapter<String> adapterSortBy = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sortOptionsArray);
        adapterSortBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(adapterSortBy);

        spinnerSortBy.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                currentSortBy = sortByValueMap.get(sortOptionsArray[position]);
                fetchAllProducts();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        llFilter.setOnClickListener(v -> showPriceFilterDialog());
        ivBack.setOnClickListener(v -> finish());

        // ĐÃ SỬA: Gọi API luôn khi vào trang
        fetchAllProducts();
    }

    private void fetchAllProducts() {
        // ĐÃ SỬA: Chuyển "" thành null để Server trả về toàn bộ
        // ĐÃ SỬA: Trả lại chuỗi rỗng "" thay vì null để Server C# không bị báo lỗi 400
        apiService.getFilteredSortedProducts(null, currentMinPrice, currentMaxPrice, currentSortBy)
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> products = response.body();

                            if (showOnlyDiscount) {
                                List<Product> discountedList = new ArrayList<>();
                                for (Product p : products) {
                                    if (p.getDiscount() > 0) discountedList.add(p);
                                }
                                products = discountedList;
                            }

                            if(products.isEmpty()) {
                                Toast.makeText(AllProductsActivity.this, "Không có sản phẩm nào phù hợp!", Toast.LENGTH_SHORT).show();
                            }

                            productAdapter = new ProductAdapter(products);
                            rvAllProducts.setAdapter(productAdapter);
                        } else {
                            // ĐÃ SỬA: In ra mã lỗi để bắt bệnh chính xác nếu Server từ chối
                            Toast.makeText(AllProductsActivity.this, "Lỗi Server: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Toast.makeText(AllProductsActivity.this, "Lỗi kết nối máy chủ!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

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
                        fetchAllProducts();
                    } catch (NumberFormatException e) {
                        Toast.makeText(AllProductsActivity.this, "Vui lòng nhập số hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CLEAR", (dialog, which) -> {
                    currentMinPrice = null;
                    currentMaxPrice = null;
                    fetchAllProducts();
                })
                .show();
    }
}