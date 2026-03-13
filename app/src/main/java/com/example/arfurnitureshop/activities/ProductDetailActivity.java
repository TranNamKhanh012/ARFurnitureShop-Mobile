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
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.CartItem;
import com.example.arfurnitureshop.models.Product;
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

    private Product currentProduct;
    // Biến lưu số lượng người dùng chọn mua
    private int selectedQuantity = 1;

    private RecyclerView rvRecommended;
    private ProductAdapter recommendedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

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

        // Ánh xạ RecyclerView đề xuất
        rvRecommended = findViewById(R.id.rvRecommended);
        rvRecommended.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        // ==========================================
        // 2. NHẬN VÀ HIỂN THỊ DỮ LIỆU SẢN PHẨM HIỆN TẠI
        // ==========================================
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        String name = getIntent().getStringExtra("PRODUCT_NAME");
        double price = getIntent().getDoubleExtra("PRODUCT_PRICE", 0);
        String imageUrl = getIntent().getStringExtra("PRODUCT_IMAGE");
        String modelUrl = getIntent().getStringExtra("PRODUCT_MODEL");

        currentProduct = new Product(productId, name, imageUrl, modelUrl, price, 0, 5.0);

        if (name != null) tvProductName.setText(name);

        NumberFormat formatVN = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvProductPrice.setText(formatVN.format(price));

        Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivProductImage);

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
        btnAddToCart.setOnClickListener(v -> {
            android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            if (!isLoggedIn) {
                Toast.makeText(ProductDetailActivity.this, "Bạn cần đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
            } else {
                int userId = prefs.getInt("USER_ID", -1);

                // DÙNG BIẾN selectedQuantity VÀO ĐÂY ĐỂ THÊM ĐÚNG SỐ LƯỢNG KHÁCH CHỌN
                CartManager.getInstance(ProductDetailActivity.this).add(new CartItem(currentProduct, selectedQuantity));
                Toast.makeText(ProductDetailActivity.this, "Đã thêm " + selectedQuantity + " " + currentProduct.getName() + " vào giỏ!", Toast.LENGTH_SHORT).show();

                // Gửi lên C#
                for (int i = 0; i < selectedQuantity; i++) {
                    ApiService.apiService.addToCart(userId, productId).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                    });
                }
            }
        });

        btnBuyNow.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển đến trang Thanh toán...", Toast.LENGTH_SHORT).show();
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
                    ApiService.apiService.removeFromWishlist(userId, currentId).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                    });
                } else {
                    com.example.arfurnitureshop.models.WishlistManager.add(currentProduct);
                    fabWishlist.setImageResource(R.drawable.ic_heart_filled);
                    ApiService.apiService.addToWishlist(userId, currentId).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                    });
                }
            }
        });

        fabArView.setOnClickListener(v -> {
            String finalModelUrl = currentProduct.getModelUrl();
            if (finalModelUrl == null || finalModelUrl.trim().isEmpty()) {
                Toast.makeText(this, "Sản phẩm này chưa có mô hình 3D!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
            Uri intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                    .appendQueryParameter("file", finalModelUrl)
                    .appendQueryParameter("mode", "ar_only")
                    .appendQueryParameter("title", currentProduct.getName())
                    .build();
            sceneViewerIntent.setData(intentUri);
            sceneViewerIntent.setPackage("com.google.ar.core");
            try { startActivity(sceneViewerIntent); }
            catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, "Điện thoại của bạn chưa cài đặt ARCore!", Toast.LENGTH_LONG).show();
            }
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