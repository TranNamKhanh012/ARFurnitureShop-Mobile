package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.PendingReviewAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingReviewsActivity extends AppCompatActivity {
    private RecyclerView rvPendingReviews;
    private TextView tvNoPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_reviews);

        rvPendingReviews = findViewById(R.id.rvPendingReviews);
        tvNoPending = findViewById(R.id.tvNoPending);
        rvPendingReviews.setLayoutManager(new LinearLayoutManager(this));

        // ==========================================
        // ÁNH XẠ VÀ CÀI ĐẶT HEADER DÙNG CHUNG
        // ==========================================
        View headerView = findViewById(R.id.headerPendingReviews);
        if (headerView != null) {
            // 1. Gán tiêu đề
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText("Chờ đánh giá");
            }

            // 2. Xử lý nút Back (Quay lại)
            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // 3. Xử lý nút Home (Về trang chủ)
            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(PendingReviewsActivity.this, MainActivity.class);
                    // Dọn dẹp RAM, quay thẳng về màn hình chính
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        loadPendingReviews();
    }

    private void loadPendingReviews() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", -1);
        String fullName = prefs.getString("FULL_NAME", "Khách hàng"); // Lấy tên khách để lưu vào bảng Reviews

        if (userId == -1) return;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getPendingReviews(userId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    PendingReviewAdapter adapter = new PendingReviewAdapter(response.body(), userId, fullName);
                    rvPendingReviews.setAdapter(adapter);
                    rvPendingReviews.setVisibility(View.VISIBLE);
                    tvNoPending.setVisibility(View.GONE);
                } else {
                    rvPendingReviews.setVisibility(View.GONE);
                    tvNoPending.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(PendingReviewsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}