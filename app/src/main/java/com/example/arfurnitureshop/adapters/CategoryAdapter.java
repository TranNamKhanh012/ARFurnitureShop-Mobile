package com.example.arfurnitureshop.adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.activities.CategoryProductsActivity;
import com.example.arfurnitureshop.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;

    // Constructor nhận danh sách dữ liệu
    public CategoryAdapter(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện item_category.xml cho mỗi ô
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        // Hiển thị tên và ảnh danh mục
        holder.tvCategoryName.setText(category.getName());

        android.content.Context context = holder.itemView.getContext();
        String fullImageUrl = "http://trannamkhanh-001-site1.jtempurl.com/images/" + category.getImageUrl();

        // Điền User/Pass màu cam của bạn vào đây
        String userCam = "11300735";
        String passCam = "60-dayfreetrial";
        String credential = okhttp3.Credentials.basic(userCam, passCam);

        com.bumptech.glide.load.model.GlideUrl glideUrlWithAuth = new com.bumptech.glide.load.model.GlideUrl(fullImageUrl,
                new com.bumptech.glide.load.model.LazyHeaders.Builder()
                        .addHeader("Authorization", credential)
                        .build());

        // Load ảnh vào biến holder.imgCategory (Nhớ đổi tên biến imgCategory cho đúng với file của bạn)
        Glide.with(context)
                .load(glideUrlWithAuth)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imgCategory);

        // --- SỰ KIỆN CLICK: Chuyển sang màn hình lọc sản phẩm theo Category ---
        holder.itemView.setOnClickListener(v -> {
            Log.d("CLICK_CHECK", "Đã bấm vào danh mục: " + category.getName());

            // [ĐÃ SỬA]: Xóa dòng "Context context = v.getContext();" ở đây đi
            // Cứ thế dùng luôn chữ "context" đã được khai báo ở phía trên (chỗ Glide)
            Intent intent = new Intent(context, CategoryProductsActivity.class);
            intent.putExtra("CATEGORY_ID", category.getId());
            intent.putExtra("CATEGORY_NAME", category.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (categoryList != null) {
            return categoryList.size();
        }
        return 0;
    }

    // Lớp nội (Inner class) để ánh xạ các view trong item_category.xml
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }


}