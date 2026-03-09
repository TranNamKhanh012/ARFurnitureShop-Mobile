package com.example.arfurnitureshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService; // Import ApiService của bạn
import com.example.arfurnitureshop.models.CartItem;
import com.example.arfurnitureshop.utils.CartManager;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> list;
    private Runnable onUpdate;

    public CartAdapter(List<CartItem> list, Runnable onUpdate) {
        this.list = list;
        this.onUpdate = onUpdate;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        return new CartViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_cart, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder h, int pos) {
        CartItem i = list.get(pos);
        DecimalFormat df = new DecimalFormat("#,###");
        h.name.setText(i.getProduct().getName());
        h.price.setText("₫ " + df.format(i.getProduct().getPrice()) + " VND");
        h.qty.setText(String.valueOf(i.getQuantity()));
        Glide.with(h.itemView.getContext()).load(i.getProduct().getImageUrl()).into(h.img);

        // Nút Tăng số lượng
        h.plus.setOnClickListener(v -> {
            i.setQuantity(i.getQuantity() + 1);
            CartManager.getInstance(v.getContext()).add(i);
            notifyDataSetChanged();
            onUpdate.run();
        });

        // Nút Giảm số lượng
        h.minus.setOnClickListener(v -> {
            if (i.getQuantity() > 1) {
                i.setQuantity(i.getQuantity() - 1);
                CartManager.getInstance(v.getContext()).add(i);
                notifyDataSetChanged();
                onUpdate.run();
            }
        });

        // ==========================================
        // NÚT XÓA SẢN PHẨM KHỎI GIỎ HÀNG (ĐÃ GHÉP API CÓ USER ID)
        // ==========================================
        h.del.setOnClickListener(v -> {
            int currentPos = h.getAdapterPosition(); // Lấy vị trí thực tế hiện tại
            if (currentPos == RecyclerView.NO_POSITION) return; // Chống lỗi crash khi click nhanh

            // 1. Mở bộ nhớ ra lấy ID của người dùng đang đăng nhập
            android.content.Context context = v.getContext();
            android.content.SharedPreferences prefs = context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
            int userId = prefs.getInt("USER_ID", -1);
            int productId = i.getProduct().getId();

            // 2. Xóa ở giao diện và CartManager nội bộ trước để app phản hồi nhanh
            CartManager.getInstance(context).remove(productId);
            list.remove(currentPos);
            notifyItemRemoved(currentPos); // Dùng cái này để có hiệu ứng xóa mượt mà
            onUpdate.run(); // Cập nhật lại tổng tiền

            // 3. GỌI API XÓA KHỎI DATABASE SQL SERVER VỚI ĐỦ 2 ID
            if (userId != -1) {
                ApiService.apiService.removeFromCart(userId, productId).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        // Thành công: Database C# đã cập nhật
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        // Lỗi: Ví dụ tắt mạng hoặc Server sập
                        Toast.makeText(context, "Lỗi đồng bộ xóa với Server C#!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView img, del;
        TextView name, price, qty, plus, minus;

        public CartViewHolder(View v) {
            super(v);
            img = v.findViewById(R.id.imgCartProduct);
            del = v.findViewById(R.id.ivDelete);
            name = v.findViewById(R.id.tvCartProductName);
            price = v.findViewById(R.id.tvCartProductPrice);
            qty = v.findViewById(R.id.tvQuantity);
            plus = v.findViewById(R.id.tvPlus);
            minus = v.findViewById(R.id.tvMinus);
        }
    }
}