package com.example.arfurnitureshop.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.activities.PendingReviewsActivity;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHelper {

    // 1. Hàm cài đặt sự kiện bấm vào nút chuông
    public static void setupNotificationBell(Activity activity) {
        FrameLayout layoutNotificationBell = activity.findViewById(R.id.layoutNotificationBell);
        if (layoutNotificationBell != null) {
            layoutNotificationBell.setOnClickListener(v -> {
                Intent intent = new Intent(activity, PendingReviewsActivity.class);
                activity.startActivity(intent);
            });
        }
    }

    // 2. Hàm gọi API đếm số lượng thông báo và hiển thị "số đỏ"
    public static void checkPendingReviews(Activity activity) {
        android.content.SharedPreferences prefs = activity.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", -1);
        if (userId == -1) return;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getPendingReviews(userId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int pendingCount = response.body().size();
                    TextView tvBadge = activity.findViewById(R.id.tvNotificationBadge);

                    if (tvBadge != null) {
                        if (pendingCount > 0) {
                            tvBadge.setVisibility(View.VISIBLE);
                            tvBadge.setText(String.valueOf(pendingCount));
                        } else {
                            tvBadge.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {}
        });
    }
}