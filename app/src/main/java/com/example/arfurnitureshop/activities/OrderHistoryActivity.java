package com.example.arfurnitureshop.activities;

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

        ImageView ivBack = findViewById(R.id.ivBackHistory);
        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        tvNoOrders = findViewById(R.id.tvNoOrders);

        ivBack.setOnClickListener(v -> finish());
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));

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
                    Toast.makeText(OrderHistoryActivity.this, "Mã lỗi Server: " + response.code(), Toast.LENGTH_LONG).show();
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