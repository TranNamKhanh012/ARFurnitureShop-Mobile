package com.example.arfurnitureshop.utils;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.arfurnitureshop.R;
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
                    Toast.makeText(activity, "My Profile", Toast.LENGTH_SHORT).show();
                    // Nếu muốn chuyển trang thì dùng: activity.startActivity(new Intent(activity, AccountActivity.class));
                } else if (id == R.id.nav_setting) {
                    Toast.makeText(activity, "Settings", Toast.LENGTH_SHORT).show();
                }
                // Thêm các nút About Us, Contact Us... ở đây

                // Đóng menu sau khi bấm xong
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }
}