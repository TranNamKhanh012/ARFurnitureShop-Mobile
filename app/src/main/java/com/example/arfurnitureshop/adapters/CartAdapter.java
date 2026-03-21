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
    private com.example.arfurnitureshop.api.ApiService apiService;
    public CartAdapter(List<CartItem> list, Runnable onUpdate) {
        this.list = list;
        this.onUpdate = onUpdate;
        this.apiService = com.example.arfurnitureshop.api.RetrofitClient.getClient()
                .create(com.example.arfurnitureshop.api.ApiService.class);
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
        // ========================================================
        // 1. CHUẨN BỊ LINK VÀ CHÌA KHÓA CHO GLIDE
        // ========================================================
        android.content.Context context = h.itemView.getContext();
        String fullImageUrl = "http://trannamkhanh-001-site1.jtempurl.com/images/" + i.getProduct().getImageUrl();

        String userCam = "TÊN_ĐĂNG_NHẬP_MÀU_CAM"; // <-- Nhớ thay bằng User màu cam của bạn
        String passCam = "MẬT_KHẨU_MÀU_CAM";      // <-- Nhớ thay bằng Pass màu cam của bạn
        String credential = okhttp3.Credentials.basic(userCam, passCam);

        com.bumptech.glide.load.model.GlideUrl glideUrlWithAuth = new com.bumptech.glide.load.model.GlideUrl(fullImageUrl,
                new com.bumptech.glide.load.model.LazyHeaders.Builder()
                        .addHeader("Authorization", credential)
                        .build());

        // 2. TẢI ẢNH VÀO BIẾN h.img CỦA BẠN
        Glide.with(context)
                .load(glideUrlWithAuth)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(h.img);

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
        // NÚT XÓA SẢN PHẨM KHỎI GIỎ HÀNG
        // ==========================================
        h.del.setOnClickListener(v -> {
            int currentPos = h.getAdapterPosition();
            if (currentPos == RecyclerView.NO_POSITION) return;

            // 1. Mở bộ nhớ ra lấy ID của người dùng đang đăng nhập
            // [ĐÃ SỬA]: Xóa dòng khai báo context bị trùng ở đây đi
            // Cứ thế dùng luôn chữ "context" (nó sẽ tự lấy biến context từ chỗ Glide thả xuống)
            android.content.SharedPreferences prefs = context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
            int userId = prefs.getInt("USER_ID", -1);
            int productId = i.getProduct().getId();

            // 2. Xóa ở giao diện và CartManager nội bộ trước...
            CartManager.getInstance(context).remove(productId);
            list.remove(currentPos);
            notifyItemRemoved(currentPos);
            onUpdate.run();

            // 3. GỌI API XÓA KHỎI DATABASE SQL SERVER
            if (userId != -1) {
                apiService.removeFromCart(userId, productId).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {}

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Lỗi đồng bộ xóa với Server!", Toast.LENGTH_SHORT).show();
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