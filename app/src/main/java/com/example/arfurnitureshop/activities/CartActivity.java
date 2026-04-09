package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.CartAdapter;
import com.example.arfurnitureshop.utils.CartManager;
import com.example.arfurnitureshop.utils.MenuHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rv;
    private TextView tvTotal;
    private Button btnContinue;
    private LinearLayout checkoutSection, layoutEmptyCart; // ĐÃ THÊM KHAI BÁO BIẾN
    private double currentTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rv = findViewById(R.id.rvCartItems);
        tvTotal = findViewById(R.id.tvTotalAmount);
        btnContinue = findViewById(R.id.btnContinue);
        checkoutSection = findViewById(R.id.checkoutSection); // ÁNH XẠ
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart); // ÁNH XẠ

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new CartAdapter(CartManager.getInstance(this).getItems(), this::updateTotal));

        updateTotal();

        btnContinue.setOnClickListener(v -> {
            if (currentTotal > 0) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("TOTAL_PRICE", currentTotal);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            }
        });

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        ImageView ivMenu = findViewById(R.id.ivMenu);

        MenuHelper.setupMenu(this, drawerLayout, ivMenu, navigationView);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_cart);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == bottomNav.getSelectedItemId()) { return true; }

            Intent intent = null;
            if (itemId == R.id.nav_home) {
                intent = new Intent(this, MainActivity.class);
            } else if (itemId == R.id.nav_category) {
                intent = new Intent(this, AllCategoriesActivity.class);
            } else if (itemId == R.id.nav_cart) {
                intent = new Intent(this, CartActivity.class);
            } else if (itemId == R.id.nav_wishlist) {
                intent = new Intent(this, WishlistActivity.class);
            } else if (itemId == R.id.nav_account) {
                intent = new Intent(this, AccountActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });

        com.example.arfurnitureshop.utils.SearchHelper.setupSearch(this);
        com.example.arfurnitureshop.utils.NotificationHelper.setupNotificationBell(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        com.example.arfurnitureshop.utils.BadgeUtils.loadCachedBadges(this, bottomNav);
        com.example.arfurnitureshop.utils.NotificationHelper.checkPendingReviews(this);
    }

    private void updateTotal() {
        DecimalFormat df = new DecimalFormat("#,###");
        double total = CartManager.getInstance(this).getTotal();

        currentTotal = total;
        tvTotal.setText("₫ " + df.format(total) + " VND");

        // ========================================================
        // ĐÃ SỬA: ẨN/HIỆN GIAO DIỆN VÀ PHẦN TÍNH TIỀN THEO TRẠNG THÁI
        // ========================================================
        if (CartManager.getInstance(this).getItems().isEmpty()) {
            rv.setVisibility(View.GONE);
            layoutEmptyCart.setVisibility(View.VISIBLE); // Hiện câu báo trống
            checkoutSection.setVisibility(View.GONE);    // Ẩn khung tổng tiền
        } else {
            rv.setVisibility(View.VISIBLE);
            layoutEmptyCart.setVisibility(View.GONE);    // Ẩn câu báo trống
            checkoutSection.setVisibility(View.VISIBLE); // Hiện lại khung tổng tiền
        }
    }
}