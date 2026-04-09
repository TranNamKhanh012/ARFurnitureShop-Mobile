package com.example.arfurnitureshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.models.OrderDetailResponse;
import java.text.DecimalFormat;
import java.util.List;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder> {
    private List<OrderDetailResponse.OrderItem> items;

    public OrderProductAdapter(List<OrderDetailResponse.OrderItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailResponse.OrderItem item = items.get(position);
        DecimalFormat df = new DecimalFormat("#,###");

        holder.tvName.setText(item.getProductName());
        holder.tvQty.setText("x " + item.getQuantity());
        holder.tvPrice.setText("₫ " + df.format(item.getUnitPrice()));

        if (item.getSelectedSize() != null && !item.getSelectedSize().isEmpty()) {
            holder.tvSize.setVisibility(View.VISIBLE);
            holder.tvSize.setText("Size: " + item.getSelectedSize());
        } else {
            holder.tvSize.setVisibility(View.GONE);
        }

        // ========================================================
        // ĐÃ SỬA: CHUẨN BỊ LINK VÀ CHÌA KHÓA CHO GLIDE LẤY ẢNH TRÊN MẠNG
        // ========================================================
        String fullImageUrl = "http://trannamkhanh-001-site1.jtempurl.com/images/" + item.getProductImage();

        String userCam = "11300735";
        String passCam = "60-dayfreetrial";
        String credential = okhttp3.Credentials.basic(userCam, passCam);

        com.bumptech.glide.load.model.GlideUrl glideUrlWithAuth = new com.bumptech.glide.load.model.GlideUrl(fullImageUrl,
                new com.bumptech.glide.load.model.LazyHeaders.Builder()
                        .addHeader("Authorization", credential)
                        .build());

        // TẢI ẢNH VÀO BIẾN holder.ivProduct
        Glide.with(holder.itemView.getContext())
                .load(glideUrlWithAuth)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivProduct);
        // ========================================================
    }


    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvSize, tvPrice, tvQty;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivDetailProduct);
            tvName = itemView.findViewById(R.id.tvDetailName);
            tvSize = itemView.findViewById(R.id.tvDetailSize);
            tvPrice = itemView.findViewById(R.id.tvDetailPrice);
            tvQty = itemView.findViewById(R.id.tvDetailQty);
        }
    }
}