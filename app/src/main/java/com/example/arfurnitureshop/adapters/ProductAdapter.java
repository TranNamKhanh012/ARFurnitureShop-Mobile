package com.example.arfurnitureshop.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.activities.CartActivity;
import com.example.arfurnitureshop.models.CartItem;
import com.example.arfurnitureshop.models.Product;
import com.example.arfurnitureshop.utils.CartManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private com.example.arfurnitureshop.api.ApiService apiService; // <-- THÊM DÒNG NÀY

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
        // Khởi tạo apiService thông qua RetrofitClient đã có chìa khóa vượt rào
        this.apiService = com.example.arfurnitureshop.api.RetrofitClient.getClient()
                .create(com.example.arfurnitureshop.api.ApiService.class);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        android.content.Context context = holder.itemView.getContext();

        // 1. Tên sản phẩm
        holder.tvProductName.setText(product.getName());

        // 2. XỬ LÝ VẼ NGÔI SAO TỪ DATABASE
        double rating = product.getRating();
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) stars.append("★");
            else stars.append("☆");
        }
        holder.tvRating.setText(stars.toString() + " " + rating);

        // 3. XỬ LÝ ẨN/HIỆN GIẢM GIÁ VÀ TÍNH TIỀN
        java.text.NumberFormat formatter = new java.text.DecimalFormat("#,###");

        // [ĐÃ SỬA Ở ĐÂY - BƯỚC 1]: Khai báo biến formattedPrice dùng chung cho cả giá hiển thị và bottom sheet
        String formattedPrice = formatter.format(product.getFinalPrice()) + " ₫";

        if (product.getDiscount() > 0) {
            holder.tvSaleTag.setVisibility(View.VISIBLE);
            holder.tvSaleTag.setText("-" + product.getDiscount() + "%");

            // [ĐÃ SỬA Ở ĐÂY]: Gán bằng biến vừa tạo
            holder.tvPrice.setText(formattedPrice);

            holder.tvOldPrice.setVisibility(View.VISIBLE);
            holder.tvOldPrice.setText(formatter.format(product.getPrice()) + " ₫");
            holder.tvOldPrice.setPaintFlags(holder.tvOldPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvSaleTag.setVisibility(View.GONE);
            holder.tvOldPrice.setVisibility(View.GONE);

            // [ĐÃ SỬA Ở ĐÂY]: Gán bằng biến vừa tạo
            holder.tvPrice.setText(formattedPrice);
        }

        // 1. Link ảnh gốc của bạn
        String fullImageUrl = "http://trannamkhanh-001-site1.jtempurl.com/images/" + product.getImageUrl();

// 2. TẠO CHÌA KHÓA MÀU CAM CHO GLIDE (Điền đúng User/Pass của SmarterASP vào đây nhé)
        String userCam = "11300735"; // <-- SỬA CHỖ NÀY
        String passCam = "60-dayfreetrial";      // <-- SỬA CHỖ NÀY
        String credential = okhttp3.Credentials.basic(userCam, passCam);

// 3. Gắn chìa khóa vào link ảnh bằng GlideUrl
        com.bumptech.glide.load.model.GlideUrl glideUrlWithAuth = new com.bumptech.glide.load.model.GlideUrl(fullImageUrl,
                new com.bumptech.glide.load.model.LazyHeaders.Builder()
                        .addHeader("Authorization", credential)
                        .build());

// 4. Load ảnh bằng biến glideUrlWithAuth thay vì String
        Glide.with(context)
                .load(glideUrlWithAuth) // <--- TRUYỀN BIẾN MỚI NÀY VÀO ĐÂY
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.imgProduct);

        // --- SỰ KIỆN CLICK TOÀN BỘ SẢN PHẨM ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.example.arfurnitureshop.activities.ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            intent.putExtra("PRODUCT_NAME", product.getName());
            intent.putExtra("PRODUCT_PRICE", product.getPrice());
            intent.putExtra("PRODUCT_IMAGE", product.getImageUrl());
            intent.putExtra("PRODUCT_MODEL", product.getModelUrl());
            intent.putExtra("PRODUCT_SIZES", product.getSizes());
            intent.putExtra("PRODUCT_DISCOUNT", product.getDiscount());
            context.startActivity(intent);
        });

        // --- KIỂM TRA TRẠNG THÁI TIM LÚC MỞ ---
        boolean isFav = com.example.arfurnitureshop.models.WishlistManager.isFavorite(product.getId());
        if (isFav) {
            holder.ivWishlist.setImageResource(R.drawable.ic_heart_filled);
        } else {
            holder.ivWishlist.setImageResource(R.drawable.ic_heart_empty);
        }

        // --- SỰ KIỆN BẤM TIM ---
        holder.ivWishlist.setOnClickListener(v -> {
            android.content.SharedPreferences prefs = context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            if (!isLoggedIn) {
                Toast.makeText(context, "Vui lòng đăng nhập để lưu Yêu thích!", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, com.example.arfurnitureshop.activities.LoginActivity.class));
            } else {
                int userId = prefs.getInt("USER_ID", -1);
                int productId = product.getId();
                boolean currentFav = com.example.arfurnitureshop.models.WishlistManager.isFavorite(productId);

                if (currentFav) {
                    com.example.arfurnitureshop.models.WishlistManager.remove(productId);
                    holder.ivWishlist.setImageResource(R.drawable.ic_heart_empty);
                    if (context instanceof com.example.arfurnitureshop.activities.WishlistActivity) {
                        notifyDataSetChanged();
                    }
                    apiService.removeFromWishlist(userId, productId).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            Toast.makeText(context, "Đã bỏ Yêu thích!", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                    });
                } else {
                    com.example.arfurnitureshop.models.WishlistManager.add(product);
                    holder.ivWishlist.setImageResource(R.drawable.ic_heart_filled);
                    apiService.addToWishlist(userId, productId).enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                            Toast.makeText(context, "Đã thêm vào Wishlist!", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                    });
                }
            }
        });

        // --- SỰ KIỆN BẤM NÚT ADD TO CART BÊN NGOÀI ---
        holder.btnAddToCart.setOnClickListener(v -> {
            // [ĐÃ SỬA Ở ĐÂY - BƯỚC 2]: Truyền biến formattedPrice vào cửa sổ mua hàng (hết báo đỏ)
            showAddToCartBottomSheet(context, product, formattedPrice);
        });
    }

    // HÀM HIỂN THỊ CỬA SỔ BOTTOM SHEET
    private void showAddToCartBottomSheet(Context context, Product product, String initialPriceFormatted) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart, null);
        bottomSheetDialog.setContentView(dialogView);

        ImageView imgProduct = dialogView.findViewById(R.id.dialogProductImage);
        TextView tvName = dialogView.findViewById(R.id.dialogProductName);
        TextView tvPrice = dialogView.findViewById(R.id.dialogProductPrice);
        TextView btnMinus = dialogView.findViewById(R.id.dialogBtnMinus);
        TextView btnPlus = dialogView.findViewById(R.id.dialogBtnPlus);
        TextView tvQuantity = dialogView.findViewById(R.id.dialogTvQuantity);
        Button btnAddCartDialog = dialogView.findViewById(R.id.dialogBtnAddToCart);
        Button btnBuyNow = dialogView.findViewById(R.id.dialogBtnBuyNow);

        tvName.setText(product.getName());
        tvPrice.setText(initialPriceFormatted);
        Glide.with(context).load(product.getImageUrl()).into(imgProduct);

        final int[] quantity = {1};
        NumberFormat formatter = new DecimalFormat("#,###");

        Runnable updateUI = () -> {
            tvQuantity.setText(String.valueOf(quantity[0]));
            // [ĐÃ SỬA Ở ĐÂY - BƯỚC 3 QUAN TRỌNG]: Phải nhân với giá ĐÃ GIẢM (getFinalPrice) chứ không phải giá gốc
            double totalPrice = product.getFinalPrice() * quantity[0];
            tvPrice.setText(formatter.format(totalPrice) + " ₫");
        };

        btnMinus.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                updateUI.run();
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity[0]++;
            updateUI.run();
        });

        btnAddCartDialog.setOnClickListener(v -> {
            android.content.SharedPreferences prefs = context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            if (!isLoggedIn) {
                Toast.makeText(context, "Vui lòng đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, com.example.arfurnitureshop.activities.LoginActivity.class));
                bottomSheetDialog.dismiss();
            } else {
                int userId = prefs.getInt("USER_ID", -1);

                // 1. Lưu vào SQLite trên máy
                CartManager.getInstance(context).add(new CartItem(product, quantity[0], ""));
                Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();

                // 2. GỌI API 1 LẦN DUY NHẤT (Bỏ vòng lặp for đi)
                if (userId != -1) {
                    // Truyền đủ 4 tham số: userId, productId, quantity[0], và chuỗi rỗng "" cho size
                    apiService.addToCart(userId, product.getId(), quantity[0], "").enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {}
                    });
                }
                bottomSheetDialog.dismiss();
            }
        });

        btnBuyNow.setOnClickListener(v -> {
            Toast.makeText(context, "Đang tiến hành thanh toán...", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, ivWishlist;
        Button btnAddToCart;
        TextView tvProductName, tvPrice, tvRating, tvSaleTag, tvOldPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            ivWishlist = itemView.findViewById(R.id.ivWishlist);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);

            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvSaleTag = itemView.findViewById(R.id.tvSaleTag);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
        }
    }
}