package com.example.arfurnitureshop.utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.activities.AboutActivity;
import com.example.arfurnitureshop.activities.ContactUsActivity; // ĐÃ THÊM: Import trang Liên hệ
import com.example.arfurnitureshop.activities.MainActivity;
import com.example.arfurnitureshop.activities.ProfileActivity;
import com.example.arfurnitureshop.activities.SettingsActivity;
import com.google.android.material.navigation.NavigationView;

public class MenuHelper {

    // Hàm tĩnh này sẽ được gọi ở bất kỳ trang nào có Menu
    public static void setupMenu(Activity activity, DrawerLayout drawerLayout, ImageView ivMenu, NavigationView navigationView) {

        if (ivMenu != null && drawerLayout != null) {
            // Sự kiện bấm nút 3 gạch mở Menu
            ivMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        if (navigationView != null) {
            // Sự kiện bấm vào các mục trong Menu
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    activity.startActivity(intent);
                }
                else if (id == R.id.nav_home) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    // Xóa các trang cũ để đỡ nặng máy khi về Home
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                }
                else if (id == R.id.nav_about) {
                    Intent intent = new Intent(activity, AboutActivity.class);
                    activity.startActivity(intent);
                }
                else if (id == R.id.nav_setting) {
                    Intent intent = new Intent(activity, SettingsActivity.class);
                    activity.startActivity(intent);
                }
                else if (id == R.id.nav_order_history) { // <--- Đổi R.id.nav_history thành đúng ID trong file drawer_menu.xml của bạn
                    Intent intent = new Intent(activity, com.example.arfurnitureshop.activities.OrderHistoryActivity.class);
                    activity.startActivity(intent);
                }
                // ==========================================
                // ĐÃ THÊM: Xử lý khi bấm nút Liên hệ
                // ==========================================
                else if (id == R.id.nav_contact) {
                    Intent intent = new Intent(activity, ContactUsActivity.class);
                    activity.startActivity(intent);
                }

                // Đóng menu sau khi bấm xong
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }
}