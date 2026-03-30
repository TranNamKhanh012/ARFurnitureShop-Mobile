package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    private com.example.arfurnitureshop.api.ApiService apiService;
    private Product currentProduct;
    // Biến lưu số lượng người dùng chọn mua
    private int selectedQuantity = 1;

    private RecyclerView rvRecommended;
    private ProductAdapter recommendedAdapter;
    private String selectedSize = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        apiService = com.example.arfurnitureshop.api.RetrofitClient.getClient()
                .create(com.example.arfurnitureshop.api.ApiService.class);
        // ==========================================
        // 1. ÁNH XẠ TOÀN BỘ VIEW TỪ GIAO DIỆN
        // ==========================================
        ImageView ivBack = findViewById(R.id.ivBack);
        ImageView ivProductImage = findViewById(R.id.ivProductImage);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        Button btnBuyNow = findViewById(R.id.btnBuyNow);
        ImageView ivCartTop = findViewById(R.id.ivCartTop);
        FloatingActionButton fabWishlist = findViewById(R.id.fabWishlist);
        FloatingActionButton fabArView = findViewById(R.id.fabArView);

        // Ánh xạ các nút số lượng
        TextView btnMinusDetail = findViewById(R.id.btnMinusDetail);
        TextView btnPlusDetail = findViewById(R.id.btnPlusDetail);
        TextView tvQuantityDetail = findViewById(R.id.tvQuantityDetail);

        androidx.recyclerview.widget.RecyclerView rvProductReviews = findViewById(R.id.rvProductReviews);
        android.widget.TextView tvEmptyReviews = findViewById(R.id.tvEmptyReviews);
        rvProductReviews.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));


        // Ánh xạ RecyclerView đề xuất
        rvRecommended = findViewById(R.id.rvRecommended);
        rvRecommended.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        // ==========================================
        // 2. NHẬN VÀ HIỂN THỊ DỮ LIỆU SẢN PHẨM HIỆN TẠI
        // ==========================================
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        String name = getIntent().getStringExtra("PRODUCT_NAME");
        double originalPrice = getIntent().getDoubleExtra("PRODUCT_PRICE", 0);
        String imageUrl = getIntent().getStringExtra("PRODUCT_IMAGE");
        String modelUrl = getIntent().getStringExtra("PRODUCT_MODEL");

        // Nhận thêm % giảm giá từ Adapter truyền sang
        int discount = getIntent().getIntExtra("PRODUCT_DISCOUNT", 0);

        currentProduct = new Product();
        currentProduct.setId(productId);
        currentProduct.setName(name);
        currentProduct.setImageUrl(imageUrl);
        currentProduct.setModelUrl(modelUrl);
        currentProduct.setPrice(originalPrice);
        currentProduct.setDiscount(discount); // Lưu discount chuẩn
        currentProduct.setRating(5.0);

        if (name != null) tvProductName.setText(name);

        // --- TÍNH TOÁN GIÁ SAU KHI GIẢM ---
        double finalPrice = originalPrice;
        if (discount > 0) {
            finalPrice = originalPrice - (originalPrice * discount / 100.0);
        }

        // --- VẼ GIÁ GẠCH NGANG TRỰC TIẾP LÊN MÀN HÌNH ---
        java.text.NumberFormat formatVN = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));
        if (discount > 0) {
            String oldPriceStr = formatVN.format(originalPrice);
            String newPriceStr = formatVN.format(finalPrice);
            String fullText = oldPriceStr + "   " + newPriceStr;

            android.text.SpannableString spannable = new android.text.SpannableString(fullText);

            // Gạch ngang và bôi xám giá cũ
            spannable.setSpan(new android.text.style.StrikethroughSpan(), 0, oldPriceStr.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.ForegroundColorSpan(android.graphics.Color.GRAY), 0, oldPriceStr.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // In đậm và tô đỏ giá mới
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
            layoutSizeSelection.setVisibility(android.view.View.VISIBLE);
            String[] sizesArray = sizesString.split(",");
            java.util.List<String> sizesList = new java.util.ArrayList<>();
            for(String s : sizesArray) sizesList.add(s.trim());

            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizesList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSizes.setAdapter(adapter);

            spinnerSizes.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                    selectedSize = sizesList.get(position);
                }
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) { }
            });
        } else if (name != null && name.toLowerCase().contains("giày")) {
            // BACKUP: Nếu API C# chưa kịp cập nhật nhưng tên là "Giày" thì tự mọc ra Size
            layoutSizeSelection.setVisibility(android.view.View.VISIBLE);
            String[] defaultSizes = {"39", "40", "41", "42", "43"};
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultSizes);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSizes.setAdapter(adapter);
            spinnerSizes.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                    selectedSize = defaultSizes[position];
                }
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) { }
            });
        }
        // ... Code cũ lấy chi tiết sản phẩm của bạn ...

        // GỌI API LẤY DANH SÁCH ĐÁNH GIÁ (Dán vào cuối hàm onCreate hoặc sau khi lấy xong Product)
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getProductReviews(productId).enqueue(new retrofit2.Callback<java.util.List<ReviewResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<ReviewResponse>> call, retrofit2.Response<java.util.List<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ProductReviewAdapter reviewAdapter = new ProductReviewAdapter(response.body());
                    rvProductReviews.setAdapter(reviewAdapter);
                    rvProductReviews.setVisibility(android.view.View.VISIBLE);
                    tvEmptyReviews.setVisibility(android.view.View.GONE);
                } else {
                    rvProductReviews.setVisibility(android.view.View.GONE);
                    tvEmptyReviews.setVisibility(android.view.View.VISIBLE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<ReviewResponse>> call, Throwable t) {}
        });

        // 1. Đã đổi tên biến thành imageName để tránh bị trùng lặp
        String imageName = getIntent().getStringExtra("PRODUCT_IMAGE");
        String fullImageUrl = "http://trannamkhanh-001-site1.jtempurl.com/images/" + imageName;

// 2. Tạo chìa khóa màu cam
        String userCam = "11300735"; // <-- Nhập đúng User của bạn
        String passCam = "60-dayfreetrial";      // <-- Nhập đúng Pass của bạn
        String credential = okhttp3.Credentials.basic(userCam, passCam);

// 3. Gắn chìa khóa vào link
        com.bumptech.glide.load.model.GlideUrl glideUrlWithAuth = new com.bumptech.glide.load.model.GlideUrl(fullImageUrl,
                new com.bumptech.glide.load.model.LazyHeaders.Builder()
                        .addHeader("Authorization", credential)
                        .build());

// 4. Load ảnh
        Glide.with(this)
                .load(glideUrlWithAuth)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivProductImage); // <--- Hãy xóa chữ này và điền đúng tên biến ImageView của bạn vào (ví dụ: ivProduct)

        // ==========================================
        // 3. XỬ LÝ SỰ KIỆN TĂNG/GIẢM SỐ LƯỢNG
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

        // ==========================================
        // 4. GỌI API LẤY SẢN PHẨM ĐỀ XUẤT
        // ==========================================
        fetchRecommendedProducts(productId);

        // ==========================================
        // 5. CÁC SỰ KIỆN NÚT BẤM (GIỎ HÀNG, TIM, AR)
        // ==========================================
        ivBack.setOnClickListener(v -> finish());
        ivCartTop.setOnClickListener(v -> startActivity(new Intent(ProductDetailActivity.this, CartActivity.class)));

        // Nút thêm vào giỏ
        // Nút thêm vào giỏ
        btnAddToCart.setOnClickListener(v -> {
            android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            if (!isLoggedIn) {
                Toast.makeText(ProductDetailActivity.this, "Bạn cần đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
            } else {
                int userId = prefs.getInt("USER_ID", -1);

                // 1. Lưu vào SQLite trên máy (như cũ)
                CartManager.getInstance(ProductDetailActivity.this).add(new CartItem(currentProduct, selectedQuantity, selectedSize));
                Toast.makeText(ProductDetailActivity.this, "Đã thêm " + selectedQuantity + " " + currentProduct.getName() + " vào giỏ!", Toast.LENGTH_SHORT).show();
                com.example.arfurnitureshop.utils.BadgeUtils.fetchAndCacheBadges(ProductDetailActivity.this);

                // 2. GỌI API 1 LẦN DUY NHẤT (Gửi kèm số lượng và Size)
                String finalSize = selectedSize != null ? selectedSize : ""; // Tránh gửi null
                apiService.addToCart(userId, productId, selectedQuantity, finalSize).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}
                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                });
            }
        });

        // ==========================================
        // 5. XỬ LÝ NÚT BUY NOW: THANH TOÁN LUÔN
        // ==========================================
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

                // [MỚI] GỬI CỜ BÁO HIỆU ĐÂY LÀ "MUA NGAY" KÈM CHI TIẾT SẢN PHẨM
                intent.putExtra("IS_BUY_NOW", true);
                intent.putExtra("BUY_NOW_ID", currentProduct.getId());
                intent.putExtra("BUY_NOW_NAME", currentProduct.getName());
                intent.putExtra("BUY_NOW_PRICE", currentProduct.getPrice());
                intent.putExtra("BUY_NOW_IMAGE", currentProduct.getImageUrl());
                intent.putExtra("BUY_NOW_QTY", selectedQuantity);
                intent.putExtra("BUY_NOW_DISCOUNT", currentProduct.getDiscount());

                // Lưu ý: Nếu code của bạn có biến selectedSize cho Giày thì thay "" bằng biến đó nhé!
                intent.putExtra("BUY_NOW_SIZE", "");

                startActivity(intent);
            }
        });

        // Nút tim và AR giữ nguyên logic cũ của bạn
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
                    apiService.removeFromWishlist(userId, currentId).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                    });
                } else {
                    com.example.arfurnitureshop.models.WishlistManager.add(currentProduct);
                    fabWishlist.setImageResource(R.drawable.ic_heart_filled);
                    apiService.addToWishlist(userId, currentId).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                    });
                }
            }
        });

        fabArView.setOnClickListener(v -> {
            // Tạm thời bỏ qua logic kiểm tra mặt hàng
            // Ép nút này chuyển thẳng sang trang Camera Face AR để Test
            Intent intent = new Intent(ProductDetailActivity.this, FaceArActivity.class);
            startActivity(intent);
        });

    }

    // Hàm gọi API nạp sản phẩm đề xuất
    private void fetchRecommendedProducts(int currentProductId) {
        RetrofitClient.getClient().create(ApiService.class).getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fullList = response.body();
                    List<Product> recommendedList = new ArrayList<>();

                    // Lọc bỏ sản phẩm hiện tại ra khỏi danh sách đề xuất
                    for (Product p : fullList) {
                        if (p.getId() != currentProductId) {
                            recommendedList.add(p);
                        }
                    }

                    // Trộn ngẫu nhiên danh sách và lấy 5 sản phẩm đầu tiên
                    Collections.shuffle(recommendedList);
                    int limit = Math.min(recommendedList.size(), 5);
                    List<Product> finalList = new ArrayList<>(recommendedList.subList(0, limit));

                    // Nạp vào RecyclerView Đề xuất (Dùng chung ProductAdapter)
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