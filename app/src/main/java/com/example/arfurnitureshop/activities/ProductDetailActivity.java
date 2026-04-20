package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.ProductAdapter;
import com.example.arfurnitureshop.adapters.ProductReviewAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.CartItem;
import com.example.arfurnitureshop.models.Product;
import com.example.arfurnitureshop.models.ReviewResponse;
import com.example.arfurnitureshop.utils.CartManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private ApiService apiService;
    private Product currentProduct;
    private int selectedQuantity = 1;

    private RecyclerView rvRecommended;
    private ProductAdapter recommendedAdapter;
    private String selectedSize = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // ==========================================
        // 1. ÁNH XẠ TOÀN BỘ VIEW TỪ GIAO DIỆN
        // ==========================================
        ImageView ivProductImage = findViewById(R.id.ivProductImage);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);

        // MỚI THÊM: Ánh xạ phần mô tả sản phẩm
        TextView tvProductDescription = findViewById(R.id.tvProductDescription);

        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        Button btnBuyNow = findViewById(R.id.btnBuyNow);
        FloatingActionButton fabWishlist = findViewById(R.id.fabWishlist);

        TextView btnMinusDetail = findViewById(R.id.btnMinusDetail);
        TextView btnPlusDetail = findViewById(R.id.btnPlusDetail);
        TextView tvQuantityDetail = findViewById(R.id.tvQuantityDetail);

        RecyclerView rvProductReviews = findViewById(R.id.rvProductReviews);
        TextView tvEmptyReviews = findViewById(R.id.tvEmptyReviews);
        rvProductReviews.setLayoutManager(new LinearLayoutManager(this));

        // ==========================================
        // 2. ÁNH XẠ HEADER DÙNG CHUNG VÀ XỬ LÝ NÚT BACK
        // ==========================================
        View headerView = findViewById(R.id.headerProductDetail);
        if (headerView != null) {
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText("Chi tiết sản phẩm");
            }

            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
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

        rvRecommended = findViewById(R.id.rvRecommended);
        rvRecommended.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        // ==========================================
        // 3. NHẬN VÀ HIỂN THỊ DỮ LIỆU SẢN PHẨM HIỆN TẠI
        // ==========================================
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        String name = getIntent().getStringExtra("PRODUCT_NAME");
        double originalPrice = getIntent().getDoubleExtra("PRODUCT_PRICE", 0);
        String imageUrl = getIntent().getStringExtra("PRODUCT_IMAGE");
        String modelUrl = getIntent().getStringExtra("PRODUCT_MODEL");
        int discount = getIntent().getIntExtra("PRODUCT_DISCOUNT", 0);

        // MỚI THÊM: Nhận dữ liệu Mô tả (Description) từ trang trước truyền sang
        String description = getIntent().getStringExtra("PRODUCT_DESCRIPTION");

        currentProduct = new Product();
        currentProduct.setId(productId);
        currentProduct.setName(name);
        currentProduct.setImageUrl(imageUrl);
        currentProduct.setModelUrl(modelUrl);
        currentProduct.setPrice(originalPrice);
        currentProduct.setDiscount(discount);
        currentProduct.setDescription(description); // Set vào model nếu cần dùng sau này
        currentProduct.setRating(5.0);

        if (name != null) tvProductName.setText(name);

        // MỚI THÊM: Hiển thị mô tả lên màn hình
        if (description != null && !description.trim().isEmpty()) {
            tvProductDescription.setText(description);
        } else {
            tvProductDescription.setText("Đang cập nhật mô tả cho sản phẩm này...");
        }

        // --- TÍNH TOÁN GIÁ SAU KHI GIẢM ---
        double finalPrice = originalPrice;
        if (discount > 0) {
            finalPrice = originalPrice - (originalPrice * discount / 100.0);
        }

        java.text.NumberFormat formatVN = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
        if (discount > 0) {
            String oldPriceStr = formatVN.format(originalPrice);
            String newPriceStr = formatVN.format(finalPrice);
            String fullText = oldPriceStr + "   " + newPriceStr;
            android.text.SpannableString spannable = new android.text.SpannableString(fullText);
            spannable.setSpan(new android.text.style.StrikethroughSpan(), 0, oldPriceStr.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.ForegroundColorSpan(android.graphics.Color.GRAY), 0, oldPriceStr.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.ForegroundColorSpan(android.graphics.Color.RED), oldPriceStr.length() + 3, fullText.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), oldPriceStr.length() + 3, fullText.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvProductPrice.setText(spannable);
        } else {
            tvProductPrice.setText(formatVN.format(originalPrice));
            tvProductPrice.setTextColor(android.graphics.Color.RED);
        }

        // KHUNG XỬ LÝ SIZE
        android.widget.LinearLayout layoutSizeSelection = findViewById(R.id.layoutSizeSelection);
        android.widget.Spinner spinnerSizes = findViewById(R.id.spinnerSizes);
        String sizesString = getIntent().getStringExtra("PRODUCT_SIZES");

        if (sizesString != null && !sizesString.trim().isEmpty()) {
            layoutSizeSelection.setVisibility(View.VISIBLE);
            String[] sizesArray = sizesString.split(",");
            List<String> sizesList = new ArrayList<>();
            for(String s : sizesArray) sizesList.add(s.trim());

            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizesList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSizes.setAdapter(adapter);

            spinnerSizes.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    selectedSize = sizesList.get(position);
                }
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) { }
            });
        } else if (name != null && name.toLowerCase().contains("giày")) {
            layoutSizeSelection.setVisibility(View.VISIBLE);
            String[] defaultSizes = {"39", "40", "41", "42", "43"};
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultSizes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSizes.setAdapter(adapter);
            spinnerSizes.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    selectedSize = defaultSizes[position];
                }
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) { }
            });
        }

        // GỌI API LẤY DANH SÁCH ĐÁNH GIÁ
        apiService.getProductReviews(productId).enqueue(new Callback<List<ReviewResponse>>() {
            @Override
            public void onResponse(Call<List<ReviewResponse>> call, Response<List<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ProductReviewAdapter reviewAdapter = new ProductReviewAdapter(response.body());
                    rvProductReviews.setAdapter(reviewAdapter);
                    rvProductReviews.setVisibility(View.VISIBLE);
                    tvEmptyReviews.setVisibility(View.GONE);
                } else {
                    rvProductReviews.setVisibility(View.GONE);
                    tvEmptyReviews.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<ReviewResponse>> call, Throwable t) {}
        });

        String imageName = getIntent().getStringExtra("PRODUCT_IMAGE");
        String fullImageUrl = "http://trannamkhanh-001-site1.jtempurl.com/images/" + imageName;

        String userCam = "11300735";
        String passCam = "60-dayfreetrial";
        String credential = okhttp3.Credentials.basic(userCam, passCam);

        com.bumptech.glide.load.model.GlideUrl glideUrlWithAuth = new com.bumptech.glide.load.model.GlideUrl(fullImageUrl,
                new com.bumptech.glide.load.model.LazyHeaders.Builder()
                        .addHeader("Authorization", credential)
                        .build());

        Glide.with(this)
                .load(glideUrlWithAuth)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivProductImage);

        // ==========================================
        // 4. XỬ LÝ SỰ KIỆN TĂNG/GIẢM SỐ LƯỢNG
        // ==========================================
        btnMinusDetail.setOnClickListener(v -> {
            if (selectedQuantity > 1) {
                selectedQuantity--;
                tvQuantityDetail.setText(String.valueOf(selectedQuantity));
            }
        });

        btnPlusDetail.setOnClickListener(v -> {
            selectedQuantity++;
            tvQuantityDetail.setText(String.valueOf(selectedQuantity));
        });

        fetchRecommendedProducts(productId);

        // ==========================================
        // 6. CÁC SỰ KIỆN NÚT BẤM
        // ==========================================
        btnAddToCart.setOnClickListener(v -> {
            android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            if (!isLoggedIn) {
                Toast.makeText(ProductDetailActivity.this, "Bạn cần đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
            } else {
                int userId = prefs.getInt("USER_ID", -1);
                CartManager.getInstance(ProductDetailActivity.this).add(new CartItem(currentProduct, selectedQuantity, selectedSize));
                Toast.makeText(ProductDetailActivity.this, "Đã thêm " + selectedQuantity + " " + currentProduct.getName() + " vào giỏ!", Toast.LENGTH_SHORT).show();
                com.example.arfurnitureshop.utils.BadgeUtils.fetchAndCacheBadges(ProductDetailActivity.this);

                String finalSize = selectedSize != null ? selectedSize : "";
                apiService.addToCart(userId, productId, selectedQuantity, finalSize).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {}
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {}
                });
            }
        });

        btnBuyNow.setOnClickListener(v -> {
            android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            if (!isLoggedIn) {
                Toast.makeText(ProductDetailActivity.this, "Vui lòng đăng nhập để mua hàng!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
            } else {
                double totalPrice = currentProduct.getPrice() * selectedQuantity;

                Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                intent.putExtra("TOTAL_PRICE", totalPrice);

                intent.putExtra("IS_BUY_NOW", true);
                intent.putExtra("BUY_NOW_ID", currentProduct.getId());
                intent.putExtra("BUY_NOW_NAME", currentProduct.getName());
                intent.putExtra("BUY_NOW_PRICE", currentProduct.getPrice());
                intent.putExtra("BUY_NOW_IMAGE", currentProduct.getImageUrl());
                intent.putExtra("BUY_NOW_QTY", selectedQuantity);
                intent.putExtra("BUY_NOW_DISCOUNT", currentProduct.getDiscount());
                String finalSize = selectedSize != null ? selectedSize : "";
                intent.putExtra("BUY_NOW_SIZE", finalSize);

                startActivity(intent);
            }
        });

        boolean isCurrentlyFav = com.example.arfurnitureshop.models.WishlistManager.isFavorite(currentProduct.getId());
        if (isCurrentlyFav) fabWishlist.setImageResource(R.drawable.ic_heart_filled);
        else fabWishlist.setImageResource(R.drawable.ic_heart_empty);

        fabWishlist.setOnClickListener(v -> {
            android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            if (!prefs.getBoolean("IS_LOGGED_IN", false)) {
                Toast.makeText(this, "Vui lòng đăng nhập để lưu Yêu thích!", Toast.LENGTH_SHORT).show();
            } else {
                int userId = prefs.getInt("USER_ID", -1);
                int currentId = currentProduct.getId();
                if (com.example.arfurnitureshop.models.WishlistManager.isFavorite(currentId)) {
                    com.example.arfurnitureshop.models.WishlistManager.remove(currentId);
                    fabWishlist.setImageResource(R.drawable.ic_heart_empty);
                    apiService.removeFromWishlist(userId, currentId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {}
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                } else {
                    com.example.arfurnitureshop.models.WishlistManager.add(currentProduct);
                    fabWishlist.setImageResource(R.drawable.ic_heart_filled);
                    apiService.addToWishlist(userId, currentId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {}
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                }
            }
        });


        // ==========================================
        // SỰ KIỆN: BẤM NÚT CAMERA ĐỂ MỞ AR THỜI GIAN THỰC
        // ==========================================
        com.google.android.material.floatingactionbutton.FloatingActionButton fabTryOn = findViewById(R.id.fabTryOn);
        if (fabTryOn != null) {
            fabTryOn.setOnClickListener(v -> {
                if (currentProduct != null) {
                    // ĐÃ ĐỔI TÊN BIẾN THÀNH arModelUrl ĐỂ KHÔNG BỊ TRÙNG VỚI BIẾN Ở TRÊN
                    String arModelUrl = currentProduct.getModelUrl();

                    // KIỂM TRA: Nếu Database chưa có link file 3D thì báo lỗi
                    if (arModelUrl == null || arModelUrl.trim().isEmpty() || arModelUrl.equals("null")) {
                        Toast.makeText(ProductDetailActivity.this, "Sản phẩm này chưa có mô hình 3D!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(ProductDetailActivity.this, FaceArActivity.class);
                        // Truyền link mô hình 3D (.glb) sang cho AR xử lý
                        intent.putExtra("PRODUCT_MODEL", arModelUrl);
                        startActivity(intent);
                    }
                }
            });
        }

    }

    private void fetchRecommendedProducts(int currentProductId) {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fullList = response.body();
                    List<Product> recommendedList = new ArrayList<>();

                    for (Product p : fullList) {
                        if (p.getId() != currentProductId) {
                            recommendedList.add(p);
                        }
                    }

                    Collections.shuffle(recommendedList);
                    int limit = Math.min(recommendedList.size(), 5);
                    List<Product> finalList = new ArrayList<>(recommendedList.subList(0, limit));

                    recommendedAdapter = new ProductAdapter(finalList);
                    rvRecommended.setAdapter(recommendedAdapter);
                    com.example.arfurnitureshop.utils.BadgeUtils.fetchAndCacheBadges(ProductDetailActivity.this);
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi nạp đề xuất: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentProduct != null){
            int productId = currentProduct.getId();
            boolean isFav = com.example.arfurnitureshop.models.WishlistManager.isFavorite(productId);
            FloatingActionButton fabWishlist = findViewById(R.id.fabWishlist);
            if (isFav) fabWishlist.setImageResource(R.drawable.ic_heart_filled);
            else fabWishlist.setImageResource(R.drawable.ic_heart_empty);
        }
    }
}