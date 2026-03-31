package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.models.UserProfile;
import com.google.android.material.textfield.TextInputEditText;
import com.example.arfurnitureshop.api.RetrofitClient;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText edtUsername, edtFullName, edtEmail;
    private Button btnSave;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các thành phần giao diện
        edtUsername = findViewById(R.id.edtUsername);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);

        // Khởi tạo Retrofit
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Lấy dữ liệu khi vừa mở màn hình
        loadUserData();

        // ==========================================
        // ÁNH XẠ VÀ CÀI ĐẶT HEADER DÙNG CHUNG
        // ==========================================
        View headerView = findViewById(R.id.headerProfile);
        if (headerView != null) {

            // 1. Đặt tiêu đề
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) {
                tvTitle.setText("Hồ sơ của tôi");
            }

            // 2. Xử lý nút Back (Quay lại)
            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // 3. XỬ LÝ NÚT HOME (Về trang chủ)
            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    // Xóa các trang trung gian để về thẳng Home cho mượt
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        // Xử lý nút Lưu hồ sơ
        btnSave.setOnClickListener(v -> saveUserData());
    }

    private int getCurrentUserId() {
        // Lấy ID người dùng đã lưu lúc đăng nhập
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return prefs.getInt("USER_ID", -1);
    }

    private void loadUserData() {
        int userId = getCurrentUserId();
        if (userId == -1) return;

        // Truyền thẳng userId vào API
        apiService.getProfile(userId).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();
                    edtUsername.setText(profile.getUsername());
                    edtFullName.setText(profile.getFullName());
                    edtEmail.setText(profile.getEmail());
                } else {
                    Toast.makeText(ProfileActivity.this, "Lỗi lấy dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData() {
        int userId = getCurrentUserId();
        if (userId == -1) return;

        String newUsername = edtUsername.getText().toString().trim();
        String newFullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (newUsername.isEmpty() || newFullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng không để trống thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfile updatedUser = new UserProfile(newUsername, newFullName, email);

        // Truyền userId và dữ liệu mới vào API
        apiService.updateProfile(userId, updatedUser).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();

                    // Lưu đè lại FullName mới vào SharedPreferences ở đây để các trang khác cập nhật theo
                    android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    prefs.edit().putString("FULL_NAME", newFullName).apply();
                } else {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}