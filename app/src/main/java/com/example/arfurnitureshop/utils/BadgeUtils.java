package com.example.arfurnitureshop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BadgeUtils {

    // 1. HÀM NÀY CHỈ ĐỂ HIỂN THỊ SỐ TỪ CACHE (Dùng ở onResume các trang)
    public static void loadCachedBadges(Context context, BottomNavigationView bottomNav) {
        if (bottomNav == null || context == null) return;

        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Lấy số đã lưu trong máy ra (mặc định là 0 nếu chưa có)
        int cartCount = prefs.getInt("CART_COUNT", 0);
        int wishlistCount = prefs.getInt("WISHLIST_COUNT", 0);

        updateBadge(context, bottomNav, R.id.nav_cart, cartCount);
        updateBadge(context, bottomNav, R.id.nav_wishlist, wishlistCount);
    }

    // 2. HÀM NÀY ĐỂ GỌI API VÀ LƯU SỐ MỚI VÀO CACHE (Chỉ gọi khi có thay đổi thật sự)
    public static void fetchAndCacheBadges(Context context) {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", -1);
        if (userId == -1) return;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Đếm giỏ hàng và lưu ngầm vào máy
        apiService.getCart(userId).enqueue(new Callback<java.util.List<com.example.arfurnitureshop.models.CartItem>>() {
            @Override
            public void onResponse(Call<java.util.List<com.example.arfurnitureshop.models.CartItem>> call, Response<java.util.List<com.example.arfurnitureshop.models.CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    prefs.edit().putInt("CART_COUNT", response.body().size()).apply();
                }
            }
            @Override
            public void onFailure(Call<java.util.List<com.example.arfurnitureshop.models.CartItem>> call, Throwable t) {}
        });

        // Đếm wishlist và lưu ngầm vào máy
        apiService.getWishlist(userId).enqueue(new Callback<java.util.List<com.example.arfurnitureshop.models.Product>>() {
            @Override
            public void onResponse(Call<java.util.List<com.example.arfurnitureshop.models.Product>> call, Response<java.util.List<com.example.arfurnitureshop.models.Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    prefs.edit().putInt("WISHLIST_COUNT", response.body().size()).apply();
                }
            }
            @Override
            public void onFailure(Call<java.util.List<com.example.arfurnitureshop.models.Product>> call, Throwable t) {}
        });
    }

    // Hàm con vẽ giao diện
    private static void updateBadge(Context context, BottomNavigationView bottomNav, int menuItemId, int count) {
        BadgeDrawable badge = bottomNav.getOrCreateBadge(menuItemId);
        if (count > 0) {
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            badge.setBadgeTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            badge.setVisible(false);
            badge.clearNumber();
        }
    }
}