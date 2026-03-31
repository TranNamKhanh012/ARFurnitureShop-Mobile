package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.OrderAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView rvOrderHistory;
    private TextView tvNoOrders;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));

        // ==========================================
        // ÁNH XẠ VÀ CÀI ĐẶT HEADER DÙNG CHUNG
        // ==========================================
        // Lưu ý: Đảm bảo trong file activity_order_history.xml của bạn
        // thẻ <include> đã được đặt id là "@+id/headerOrderHistory"
        View headerView = findViewById(R.id.headerOrderHistory);
        if (headerView != null) {

            // 1. Đặt Tiêu đề
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText("Lịch sử mua hàng");
            }

            // 2. Xử lý nút Back (Quay lại)
            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // 3. XỬ LÝ NÚT HOME (Về thẳng trang chủ)
            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
                    // Dọn dẹp RAM, xóa lịch sử các trang đè lên nhau để về Home mượt nhất
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        apiService = RetrofitClient.getClient().create(ApiService.class);
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("USER_ID", -1);
        if (userId == -1) return;

        apiService.getUserOrders(userId).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    OrderAdapter adapter = new OrderAdapter(response.body());
                    rvOrderHistory.setAdapter(adapter);
                    rvOrderHistory.setVisibility(View.VISIBLE);
                    tvNoOrders.setVisibility(View.GONE);
                } else {
                    // Nếu response code là 404 (Không tìm thấy đơn hàng) thì đừng báo lỗi rườm rà
                    // Chỉ hiển thị chữ "Chưa có đơn hàng nào" là đủ
                    if (response.code() != 404) {
                        Toast.makeText(OrderHistoryActivity.this, "Mã lỗi Server: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                    rvOrderHistory.setVisibility(View.GONE);
                    tvNoOrders.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Toast.makeText(OrderHistoryActivity.this, "Lỗi tải lịch sử: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}