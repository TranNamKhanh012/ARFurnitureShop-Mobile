package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.AddressAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.UserAddress;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAddressesActivity extends AppCompatActivity {

    private RecyclerView rvAddresses;
    private LinearLayout layoutEmpty;
    private Button btnAddAddress;

    private ApiService apiService;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        // Ánh xạ các View chính
        rvAddresses = findViewById(R.id.rvAddresses);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnAddAddress = findViewById(R.id.btnAddAddress);

        rvAddresses.setLayoutManager(new LinearLayoutManager(this));

        // ==========================================
        // ÁNH XẠ VÀ CÀI ĐẶT HEADER DÙNG CHUNG
        // ==========================================
        View headerView = findViewById(R.id.headerMyAddresses);
        if (headerView != null) {

            // 1. Đặt tiêu đề
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText("Địa chỉ của tôi");
            }

            // 2. Xử lý nút Back
            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // 3. Xử lý nút Home
            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(MyAddressesActivity.this, MainActivity.class);
                    // Xóa các trang trung gian để về Home cho mượt
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        // Khởi tạo API và lấy UserID
        apiService = RetrofitClient.getClient().create(ApiService.class);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", -1);

        btnAddAddress.setOnClickListener(v -> {
            // Chuyển sang trang Thêm Địa Chỉ Mới
            startActivity(new Intent(MyAddressesActivity.this, AddAddressActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRealAddresses(); // Tự động load lại danh sách mỗi khi mở trang
    }

    private void loadRealAddresses() {
        if (currentUserId == -1) return;

        apiService.getUserAddresses(currentUserId).enqueue(new Callback<List<UserAddress>>() {
            @Override
            public void onResponse(Call<List<UserAddress>> call, Response<List<UserAddress>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserAddress> addresses = response.body();

                    if (addresses.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvAddresses.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rvAddresses.setVisibility(View.VISIBLE);

                        // Xử lý sự kiện Edit và Delete từ Adapter truyền về
                        AddressAdapter adapter = new AddressAdapter(addresses, new AddressAdapter.OnAddressClickListener() {
                            @Override
                            public void onEdit(UserAddress address) {
                                // Gói toàn bộ dữ liệu của địa chỉ này đẩy sang trang Sửa
                                Intent intent = new Intent(MyAddressesActivity.this, AddAddressActivity.class);
                                intent.putExtra("IS_EDIT_MODE", true);
                                intent.putExtra("ADDRESS_ID", address.getId());
                                intent.putExtra("RECEIVER_NAME", address.getReceiverName());
                                intent.putExtra("PHONE_NUMBER", address.getPhoneNumber());
                                intent.putExtra("FULL_ADDRESS", address.getFullAddress());
                                intent.putExtra("IS_DEFAULT", address.isDefault());
                                startActivity(intent);
                            }

                            @Override
                            public void onDelete(UserAddress address) {
                                // Mở hộp thoại xác nhận trước khi xóa
                                new AlertDialog.Builder(MyAddressesActivity.this)
                                        .setTitle("Xóa địa chỉ")
                                        .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này?")
                                        .setPositiveButton("Xóa", (dialog, which) -> {
                                            // Gọi API C# Xóa dữ liệu
                                            apiService.deleteAddress(address.getId()).enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(Call<Void> call, Response<Void> response) {
                                                    Toast.makeText(MyAddressesActivity.this, "Đã xóa địa chỉ!", Toast.LENGTH_SHORT).show();
                                                    loadRealAddresses(); // Tải lại danh sách
                                                }
                                                @Override
                                                public void onFailure(Call<Void> call, Throwable t) {
                                                    Toast.makeText(MyAddressesActivity.this, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        })
                                        .setNegativeButton("Hủy", null)
                                        .show();
                            }
                        });
                        rvAddresses.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserAddress>> call, Throwable t) {
                Toast.makeText(MyAddressesActivity.this, "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}