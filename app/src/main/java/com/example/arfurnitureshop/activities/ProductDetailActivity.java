package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.models.Product; // Đã thêm import Product
import com.example.arfurnitureshop.utils.CartManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

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

        // ==========================================
        // 2. NHẬN VÀ HIỂN THỊ DỮ LIỆU SẢN PHẨM
        // ==========================================
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        String name = getIntent().getStringExtra("PRODUCT_NAME");
        double price = getIntent().getDoubleExtra("PRODUCT_PRICE", 0);
        String imageUrl = getIntent().getStringExtra("PRODUCT_IMAGE");

        // --- LẮP RÁP LẠI ĐỐI TƯỢNG SẢN PHẨM TỪ DỮ LIỆU ĐÃ NHẬN ---
        Product currentProduct = new Product(productId, name, imageUrl, "", price);
        // ---------------------------------------------------------

        if (name != null) tvProductName.setText(name);

        // Format giá tiền sang kiểu VNĐ (VD: 15.000.000 ₫)
        NumberFormat formatVN = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvProductPrice.setText(formatVN.format(price));

        // Tải ảnh sản phẩm bằng Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivProductImage);

        // ==========================================
        // 3. XỬ LÝ SỰ KIỆN CLICK (BẤM NÚT)
        // ==========================================

        // Nút Back (Quay lại)
        ivBack.setOnClickListener(v -> finish());

        // ==========================================
        // --- SỰ KIỆN NÚT THÊM VÀO GIỎ HÀNG ---
        // ==========================================
        btnAddToCart.setOnClickListener(v -> {
            // 1. Mở bộ nhớ ra kiểm tra xem đã đăng nhập chưa
            android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            if (!isLoggedIn) {
                // NẾU CHƯA ĐĂNG NHẬP -> Báo lỗi và đuổi sang trang Login
                Toast.makeText(ProductDetailActivity.this, "Bạn cần đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                // NẾU ĐÃ ĐĂNG NHẬP -> Lấy Mã người dùng (UserId) ra
                int userId = prefs.getInt("USER_ID", -1);

                // Gửi cả UserId và ProductId lên máy chủ C#
                ApiService.apiService.addToCart(userId, productId).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ProductDetailActivity.this, "Đã thêm " + currentProduct.getName() + " vào giỏ!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Lỗi từ Server C#!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Nút Mua ngay
        btnBuyNow.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển đến trang Thanh toán...", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
            // startActivity(intent);
        });

        // Nút xem Giỏ hàng ở góc phải trên cùng
        ivCartTop.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // ==========================================
        // --- SỰ KIỆN NÚT TRÁI TIM (WISHLIST) ---
        // ==========================================
        final boolean[] isFavorite = {false}; // Trạng thái mặc định ban đầu

        fabWishlist.setOnClickListener(v -> {
            // 1. Mở bộ nhớ ra kiểm tra đăng nhập
            android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            if (!isLoggedIn) {
                // NẾU CHƯA ĐĂNG NHẬP -> Yêu cầu đăng nhập
                Toast.makeText(ProductDetailActivity.this, "Vui lòng đăng nhập để lưu Yêu thích!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                // NẾU ĐÃ ĐĂNG NHẬP -> Tiến hành xử lý
                isFavorite[0] = !isFavorite[0]; // Đảo trạng thái

                if (isFavorite[0]) {
                    fabWishlist.setImageResource(R.drawable.ic_heart_filled);

                    // Lấy ID gửi lên API thêm vào Wishlist
                    int userId = prefs.getInt("USER_ID", -1);


                    ApiService.apiService.addToWishlist(userId, productId).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ProductDetailActivity.this, "Đã lưu vào danh sách Yêu thích!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) { }
                    });
                } else {
                    fabWishlist.setImageResource(R.drawable.ic_heart_empty);
                    Toast.makeText(ProductDetailActivity.this, "Đã bỏ Yêu thích!", Toast.LENGTH_SHORT).show();
                    // (Tùy chọn: Gọi API xóa khỏi Wishlist nếu bạn đã viết API đó)
                }
            }
        });
    }
}