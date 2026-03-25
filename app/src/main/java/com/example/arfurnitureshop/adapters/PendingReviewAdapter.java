package com.example.arfurnitureshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.Product;
import com.example.arfurnitureshop.models.ReviewRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingReviewAdapter extends RecyclerView.Adapter<PendingReviewAdapter.ViewHolder> {
    private List<Product> productList;
    private int userId;
    private String fullName;

    public PendingReviewAdapter(List<Product> productList, int userId, String fullName) {
        this.productList = productList;
        this.userId = userId;
        this.fullName = fullName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());

        // Load ảnh (Nhớ đổi lại IP localhost/SmarterASP của bạn cho đúng nhé)
        String imageUrl = "http://10.0.2.2:5103/images/" + product.getImageUrl();
        Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.ivProduct);

        // Sự kiện gửi đánh giá
        holder.btnSubmit.setOnClickListener(v -> {
            int rating = (int) holder.ratingBar.getRating();
            String comment = holder.edtComment.getText().toString().trim();

            if (comment.isEmpty()) {
                Toast.makeText(v.getContext(), "Vui lòng nhập bình luận!", Toast.LENGTH_SHORT).show();
                return;
            }

            ReviewRequest request = new ReviewRequest(product.getId(), userId, fullName, rating, comment);
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            apiService.createReview(request).enqueue(new Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(v.getContext(), "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                        // Xóa sản phẩm khỏi danh sách sau khi đánh giá xong
                        productList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(v.getContext(), "Lỗi khi gửi đánh giá!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {}
            });
        });
    }

    @Override
    public int getItemCount() { return productList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName;
        RatingBar ratingBar;
        EditText edtComment;
        Button btnSubmit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivReviewProduct);
            tvName = itemView.findViewById(R.id.tvReviewProductName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            edtComment = itemView.findViewById(R.id.edtReviewComment);
            btnSubmit = itemView.findViewById(R.id.btnSubmitReview);
        }
    }
}