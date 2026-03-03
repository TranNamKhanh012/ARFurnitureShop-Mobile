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
        if (category == null) return;

        holder.tvCategoryName.setText(category.getName());

        // Dùng Glide để load ảnh từ URL vào ImageView
        // image_151ac1.png cho thấy bạn đang thiếu bước này
        Glide.with(holder.itemView.getContext())
                .load(category.getImageUrl()) // Link lấy từ Database
                .placeholder(android.R.drawable.ic_menu_gallery) // Thêm chữ android. vào trước
                .error(android.R.drawable.ic_menu_report_image)  // Thêm chữ android. vào trước
                .into(holder.imgCategory);
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