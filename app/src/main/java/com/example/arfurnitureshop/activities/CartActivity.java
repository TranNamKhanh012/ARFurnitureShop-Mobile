package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.CartAdapter;
import com.example.arfurnitureshop.utils.CartManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.DecimalFormat;

public class CartActivity extends AppCompatActivity {
    // PHẢI KHAI BÁO BIẾN Ở ĐÂY ĐỂ HẾT ĐỎ
    private RecyclerView rv;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ánh xạ View từ layout activity_cart.xml
        rv = findViewById(R.id.rvCartItems);
        tvTotal = findViewById(R.id.tvTotalAmount);

        rv.setLayoutManager(new LinearLayoutManager(this));

        // Gắn Adapter và truyền hàm updateTotal để cập nhật tiền khi tăng/giảm số lượng
        rv.setAdapter(new CartAdapter(CartManager.getInstance(this).getItems(), this::updateTotal));

        updateTotal();

        BottomNavigationView nav = findViewById(R.id.bottomNavigationView);
        nav.setSelectedItemId(R.id.nav_cart);
        nav.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }
            return item.getItemId() == R.id.nav_cart;
        });
    }

    // PHẢI CÓ HÀM NÀY ĐỂ HẾT LỖI "this::updateTotal"
    private void updateTotal() {
        DecimalFormat df = new DecimalFormat("#,###");
        double total = CartManager.getInstance(this).getTotal();
        tvTotal.setText("₫ " + df.format(total) + " VND");
    }
}