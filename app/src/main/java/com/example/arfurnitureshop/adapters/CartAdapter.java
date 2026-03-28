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
        // ========================================================
        // 1. TÍNH TOÁN VÀ VẼ GIÁ (GẠCH NGANG NẾU CÓ GIẢM GIÁ)
        // ========================================================
        // ========================================================
        // 1. TÍNH TOÁN VÀ VẼ GIÁ (GẠCH NGANG TRÊN - GIẢM GIÁ DƯỚI)
        // ========================================================
        double originalPrice = i.getProduct().getPrice();
        int discount = i.getProduct().getDiscount();
        double finalPrice = originalPrice;
        if (discount > 0) {
            finalPrice = originalPrice - (originalPrice * discount / 100.0);
        }

        if (discount > 0) {
            // Tạo chuỗi giá định dạng
            String oldPriceStr = "₫ " + df.format(originalPrice);
            String newPriceStr = "₫ " + df.format(finalPrice);

            // [QUAN TRỌNG]: DÙNG "\n" ĐỂ XUỐNG DÒNG
            String fullText = oldPriceStr + "\n" + newPriceStr;

            android.text.SpannableString spannable = new android.text.SpannableString(fullText);

            // PHẦN 1: Giá gốc (Dòng trên)
            // - Gạch ngang và bôi xám
            spannable.setSpan(new android.text.style.StrikethroughSpan(), 0, oldPriceStr.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.ForegroundColorSpan(android.graphics.Color.GRAY), 0, oldPriceStr.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // - Làm cho nhỏ hơn một chút (ví dụ: 85%) để đẹp hơn
            spannable.setSpan(new android.text.style.RelativeSizeSpan(0.85f), 0, oldPriceStr.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // PHẦN 2: Giá giảm (Dòng dưới)
            int startOfNewPrice = oldPriceStr.length() + 1; // Bỏ qua ký tự xuống dòng "\n"
            // - Màu đỏ và in đậm
            spannable.setSpan(new android.text.style.ForegroundColorSpan(android.graphics.Color.RED), startOfNewPrice, fullText.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startOfNewPrice, fullText.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // - Làm cho lớn hơn một chút (ví dụ: 110%) để nổi bật
            spannable.setSpan(new android.text.style.RelativeSizeSpan(1.1f), startOfNewPrice, fullText.length(), android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            h.price.setText(spannable);
        } else {
            // Trường hợp không giảm giá (Vẫn hiện 1 dòng giá màu đỏ)
            h.price.setText("₫ " + df.format(originalPrice) + " VND");
            h.price.setTextColor(android.graphics.Color.RED);
            h.price.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16); // Đặt size gốc
        }
        h.qty.setText(String.valueOf(i.getQuantity()));

        // ========================================================
        // 1. XỬ LÝ HIỆN SIZE (NẾU CÓ)
        // ========================================================
        if (i.getSelectedSize() != null && !i.getSelectedSize().trim().isEmpty()) {
            h.size.setVisibility(View.VISIBLE);
            h.size.setText("Size: " + i.getSelectedSize());
        } else {
            h.size.setVisibility(View.GONE); // Nếu là kính/bàn ghế không có size thì ẩn đi
        }

        // ========================================================
        // 2. CHUẨN BỊ LINK VÀ CHÌA KHÓA CHO GLIDE
        // ========================================================
        android.content.Context context = h.itemView.getContext();
        String fullImageUrl = "http://trannamkhanh-001-site1.jtempurl.com/images/" + i.getProduct().getImageUrl();

        String userCam = "11300735"; // <-- Nhớ thay bằng User màu cam của bạn
        String passCam = "60-dayfreetrial";      // <-- Nhớ thay bằng Pass màu cam của bạn
        String credential = okhttp3.Credentials.basic(userCam, passCam);

        com.bumptech.glide.load.model.GlideUrl glideUrlWithAuth = new com.bumptech.glide.load.model.GlideUrl(fullImageUrl,
                new com.bumptech.glide.load.model.LazyHeaders.Builder()
                        .addHeader("Authorization", credential)
                        .build());

        // 3. TẢI ẢNH VÀO BIẾN h.img CỦA BẠN
        Glide.with(context)
                .load(glideUrlWithAuth)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(h.img);

        // Nút Tăng số lượng
        h.plus.setOnClickListener(v -> {
            i.setQuantity(i.getQuantity() + 1);
            CartManager.getInstance(context).add(i); // Đã update để hỗ trợ size
            notifyDataSetChanged();
            onUpdate.run();
        });

        // Nút Giảm số lượng
        h.minus.setOnClickListener(v -> {
            if (i.getQuantity() > 1) {
                i.setQuantity(i.getQuantity() - 1);
                CartManager.getInstance(context).add(i); // Đã update để hỗ trợ size
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

            // Trong Adapter, bạn đã có sẵn biến 'context' rồi, cứ dùng nó thôi!
            android.content.SharedPreferences prefs = context.getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
            int userId = prefs.getInt("USER_ID", -1);
            int productId = i.getProduct().getId();

            // [QUAN TRỌNG]: Đã thêm getSelectedSize() vào để xóa đúng loại size
            CartManager.getInstance(context).remove(productId, i.getSelectedSize());

            list.remove(currentPos);
            notifyItemRemoved(currentPos);

            if (onUpdate != null) {
                onUpdate.run();
            }

            // GỌI API XÓA KHỎI DATABASE SQL SERVER
            if (userId != -1) {
                String sizeToSend = i.getSelectedSize() != null ? i.getSelectedSize() : "";

                apiService.removeFromCart(userId, productId, sizeToSend).enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        // ĐÃ SỬA: Chờ Server xóa xong thì mới gọi hàm đếm lại, và dùng 'context' thay cho 'this'
                        com.example.arfurnitureshop.utils.BadgeUtils.fetchAndCacheBadges(context);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        android.widget.Toast.makeText(context, "Lỗi đồng bộ xóa với Server!", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });

                if (onUpdate == null) {
                    h.del.setVisibility(android.view.View.GONE);
                } else {
                    h.del.setVisibility(android.view.View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView img, del;
        // ĐÃ SỬA: Bổ sung khai báo biến "size" ở đây để hết lỗi đỏ
        TextView name, price, qty, plus, minus, size;

        public CartViewHolder(View v) {
            super(v);
            img = v.findViewById(R.id.imgCartProduct);
            del = v.findViewById(R.id.ivDelete);
            name = v.findViewById(R.id.tvCartProductName);
            price = v.findViewById(R.id.tvCartProductPrice);
            qty = v.findViewById(R.id.tvQuantity);
            plus = v.findViewById(R.id.tvPlus);
            minus = v.findViewById(R.id.tvMinus);
            size = v.findViewById(R.id.tvCartItemSize); // Hết lỗi đỏ rồi nhé!
        }
    }
}