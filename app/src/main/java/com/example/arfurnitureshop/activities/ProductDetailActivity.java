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

        // Các nút mới thêm
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

        // Nút Thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm " + name + " vào giỏ!", Toast.LENGTH_SHORT).show();
            // Code lưu vào Database sẽ viết sau
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

        // Nút Trái tim (Wishlist)
        final boolean[] isFavorite = {false}; // Trạng thái mặc định ban đầu là chưa thích

        fabWishlist.setOnClickListener(v -> {
            isFavorite[0] = !isFavorite[0]; // Đảo trạng thái

            if (isFavorite[0]) {
                // Đổi thành trái tim đỏ
                fabWishlist.setImageResource(R.drawable.ic_heart_filled);
                Toast.makeText(this, "Đã thêm vào mục Yêu thích!", Toast.LENGTH_SHORT).show();
            } else {
                // Đổi về trái tim rỗng
                fabWishlist.setImageResource(R.drawable.ic_heart_empty);
                Toast.makeText(this, "Đã bỏ Yêu thích!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}