package com.example.arfurnitureshop.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.WishlistManager;
import com.example.arfurnitureshop.utils.CartManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // --- 1. Ánh xạ các nút ---
        ImageView ivBack = findViewById(R.id.ivBack);
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        LinearLayout btnChangePassword = findViewById(R.id.btnChangePassword);
        LinearLayout btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        // Đặt tiêu đề
        android.widget.TextView tvTitle = findViewById(R.id.tvHeaderTitle);
        tvTitle.setText("Cài đặt hệ thống");

        // Bắt sự kiện nút Back dùng chung
        android.widget.ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // --- 2. Xử lý Dark Mode ---
        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false);
        if (switchDarkMode != null) {
            switchDarkMode.setChecked(isDarkMode);
            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                sharedPreferences.edit().putBoolean("DARK_MODE", isChecked).apply();
                AppCompatDelegate.setDefaultNightMode(isChecked ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            });
        }

        // --- 3. Xử lý Đổi mật khẩu ---
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> {
                SharedPreferences userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                if (userPrefs.getInt("USER_ID", -1) != -1) {
                    startActivity(new Intent(this, ChangePasswordActivity.class));
                } else {
                    Toast.makeText(this, "Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // --- 4. Xử lý Xóa tài khoản ---
        if (btnDeleteAccount != null) {
            btnDeleteAccount.setOnClickListener(v -> checkAndShowDeleteDialog());
        }
    }

    // ==========================================
    // HÀM KIỂM TRA ĐĂNG NHẬP VÀ HIỂN THỊ CẢNH BÁO
    // ==========================================
    private void checkAndShowDeleteDialog() {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = userPrefs.getInt("USER_ID", -1); // Lấy ID của người dùng đang đăng nhập

        // 1. KIỂM TRA: Nếu chưa đăng nhập (userId = -1) thì chặn lại ngay
        if (userId == -1) {
            Toast.makeText(this, "Người dùng chưa đăng nhập để xóa!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. NẾU ĐÃ ĐĂNG NHẬP: Hiển thị hộp thoại cảnh báo
        new AlertDialog.Builder(this)
                .setTitle("Cảnh báo nguy hiểm ⚠️")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản vĩnh viễn không? Toàn bộ dữ liệu đơn hàng, giỏ hàng và lịch sử sẽ bị mất và KHÔNG THỂ khôi phục.")
                .setPositiveButton("Xóa vĩnh viễn", (dialog, which) -> {

                    // 3. GỌI API ĐỂ XÓA TÀI KHOẢN TRÊN DATABASE C#
                    deleteAccountOnServer(userId, userPrefs);

                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // ==========================================
    // HÀM GỌI API XÓA TÀI KHOẢN
    // ==========================================
    private void deleteAccountOnServer(int userId, SharedPreferences userPrefs) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.deleteAccount(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // BƯỚC 1: Xóa Database thành công -> Dọn sạch dữ liệu trên điện thoại
                    userPrefs.edit().clear().apply(); // Xóa phiên đăng nhập
                    CartManager.getInstance(SettingsActivity.this).clear(); // Dọn giỏ hàng
                    WishlistManager.clear(); // Dọn mục yêu thích

                    Toast.makeText(SettingsActivity.this, "Tài khoản của bạn đã được xóa thành công!", Toast.LENGTH_LONG).show();

                    // BƯỚC 2: Đá văng về trang Đăng nhập và đóng mọi trang cũ
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Lỗi từ server (ví dụ: đang có đơn hàng chưa giao nên C# không cho xóa)
                    Toast.makeText(SettingsActivity.this, "Không thể xóa tài khoản lúc này. Lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "Lỗi kết nối máy chủ! Vui lòng kiểm tra mạng.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}