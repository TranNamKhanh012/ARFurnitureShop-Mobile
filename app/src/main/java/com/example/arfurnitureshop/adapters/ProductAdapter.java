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

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
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
        android.content.Context context = holder.itemView.getContext(); // Khai báo context để dùng cho Dialog và Intent

        // 1. Hiển thị tên và giá sản phẩm
        holder.tvProductName.setText(product.getName());

        java.text.NumberFormat formatter = new java.text.DecimalFormat("#,###");
        String formattedPrice = formatter.format(product.getPrice()) + " VND";
        holder.tvPrice.setText(formattedPrice);

        // 2. Load ảnh bằng Glide
        com.bumptech.glide.Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.imgProduct);

        // --- 3. SỰ KIỆN CLICK TOÀN BỘ SẢN PHẨM ĐỂ XEM CHI TIẾT ---
        // Khi người dùng bấm vào hình ảnh hoặc khoảng trống trên thẻ sản phẩm
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, com.example.arfurnitureshop.activities.ProductDetailActivity.class);

            // "Đóng gói" dữ liệu để gửi sang trang Chi Tiết
            intent.putExtra("PRODUCT_ID", product.getId());
            intent.putExtra("PRODUCT_NAME", product.getName());
            intent.putExtra("PRODUCT_PRICE", product.getPrice());
            intent.putExtra("PRODUCT_IMAGE", product.getImageUrl());

            context.startActivity(intent);
        });

        final boolean[] isFavorite = {false};

        holder.ivWishlist.setOnClickListener(v -> {
            isFavorite[0] = !isFavorite[0]; // Đảo trạng thái

            if (isFavorite[0]) {
                // Đổi thành trái tim đặc
                holder.ivWishlist.setImageResource(R.drawable.ic_heart_filled);
                android.widget.Toast.makeText(holder.itemView.getContext(), "Đã thích: " + product.getName(), android.widget.Toast.LENGTH_SHORT).show();
                // TODO: Gọi API lưu sản phẩm này vào CSDL (bảng Wishlist)
            } else {
                // Đổi về trái tim rỗng
                holder.ivWishlist.setImageResource(R.drawable.ic_heart_empty);
                android.widget.Toast.makeText(holder.itemView.getContext(), "Đã bỏ thích: " + product.getName(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        // --- 4. SỰ KIỆN BẤM NÚT ADD TO CART BÊN NGOÀI (Giữ nguyên của bạn) ---
        // Khi người dùng bấm chính xác vào nút giỏ hàng nhỏ trên thẻ sản phẩm
        holder.btnAddToCart.setOnClickListener(v -> {
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

        // Hàm cập nhật tiền khi tăng/giảm số lượng
        Runnable updateUI = () -> {
            tvQuantity.setText(String.valueOf(quantity[0]));
            double totalPrice = product.getPrice() * quantity[0];
            tvPrice.setText("₫ " + formatter.format(totalPrice) + " VND");
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

        // Nút lưu vào giỏ hàng
        btnAddCartDialog.setOnClickListener(v -> {
            CartManager.getInstance(context).add(new CartItem(product, quantity[0]));
            Toast.makeText(context, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();

            // Chuyển sang màn CartActivity để xem
            context.startActivity(new Intent(context, CartActivity.class));
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

    // LỚP VIEWHOLDER ĐÃ BỔ SUNG NÚT BTNADDTOCART
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvPrice;
        Button btnAddToCart; // Khai báo nút

        ImageView ivWishlist;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart); // Ánh xạ nút
            ivWishlist = itemView.findViewById(R.id.ivWishlist);
        }
    }
}