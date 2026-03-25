package com.example.arfurnitureshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.models.ReviewResponse;
import java.util.List;

public class ProductReviewAdapter extends RecyclerView.Adapter<ProductReviewAdapter.ViewHolder> {
    private List<ReviewResponse> reviewList;

    public ProductReviewAdapter(List<ReviewResponse> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewResponse review = reviewList.get(position);
        holder.tvName.setText(review.getFullName() != null ? review.getFullName() : "Khách hàng");
        holder.tvComment.setText(review.getComment());
        holder.ratingBar.setRating(review.getRating());

        // Cắt chuỗi lấy ngày (YYYY-MM-DD)
        String date = review.getCreatedAt();
        if (date != null && date.contains("T")) {
            date = date.substring(0, date.indexOf("T"));
        }
        holder.tvDate.setText(date);
    }

    @Override
    public int getItemCount() { return reviewList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvComment, tvDate;
        RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvReviewerName);
            tvComment = itemView.findViewById(R.id.tvReviewComment);
            tvDate = itemView.findViewById(R.id.tvReviewDate);
            ratingBar = itemView.findViewById(R.id.rbReviewRating);
        }
    }
}