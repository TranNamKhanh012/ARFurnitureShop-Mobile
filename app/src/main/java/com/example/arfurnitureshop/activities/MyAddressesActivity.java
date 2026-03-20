package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
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
    private ImageView ivBack;

    private ApiService apiService;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        rvAddresses = findViewById(R.id.rvAddresses);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        ivBack = findViewById(R.id.ivBack);

        rvAddresses.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo API và lấy UserID
        apiService = RetrofitClient.getClient().create(ApiService.class);
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", -1);

        ivBack.setOnClickListener(v -> finish());

        btnAddAddress.setOnClickListener(v -> {
            // ĐÃ SỬA: Chuyển sang trang Thêm Địa Chỉ Mới
            startActivity(new android.content.Intent(MyAddressesActivity.this, AddAddressActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRealAddresses(); // Tự động load lại danh sách mỗi khi mở trang
    }

    // Thêm thư viện này ở trên cùng:
// import androidx.appcompat.app.AlertDialog;
// import android.content.Intent;

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

                        // ĐÃ SỬA: Xử lý sự kiện Edit và Delete từ Adapter truyền về
                        AddressAdapter adapter = new AddressAdapter(addresses, new AddressAdapter.OnAddressClickListener() {
                            @Override
                            public void onEdit(UserAddress address) {
                                // Gói toàn bộ dữ liệu của địa chỉ này đẩy sang trang Sửa
                                android.content.Intent intent = new android.content.Intent(MyAddressesActivity.this, AddAddressActivity.class);
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
                                new androidx.appcompat.app.AlertDialog.Builder(MyAddressesActivity.this)
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