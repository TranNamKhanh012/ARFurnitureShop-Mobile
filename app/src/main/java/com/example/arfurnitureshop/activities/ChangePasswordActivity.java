package com.example.arfurnitureshop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // ==========================================
        // 1. ÁNH XẠ HEADER DÙNG CHUNG
        // ==========================================
        View headerView = findViewById(R.id.headerChangePassword);
        if (headerView != null) {
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) tvTitle.setText("Đổi Mật Khẩu");

            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) btnBack.setOnClickListener(v -> finish());

            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        // ==========================================
        // 2. ÁNH XẠ CÁC VIEW NHẬP LIỆU
        // ==========================================
        EditText edtOldPassword = findViewById(R.id.edtOldPassword);
        EditText edtNewPassword = findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        Button btnSavePassword = findViewById(R.id.btnSavePassword);

        // ==========================================
        // 3. XỬ LÝ KHI BẤM NÚT CẬP NHẬT
        // ==========================================
        if (btnSavePassword != null) {
            btnSavePassword.setOnClickListener(v -> {
                String oldPass = edtOldPassword.getText().toString().trim();
                String newPass = edtNewPassword.getText().toString().trim();
                String confirmPass = edtConfirmPassword.getText().toString().trim();

                // Kiểm tra nhập liệu trống
                if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // --- ĐIỀU KIỆN MỚI THÊM: Ít nhất 6 ký tự ---
                if (newPass.length() < 6) {
                    Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu xác nhận
                if (!newPass.equals(confirmPass)) {
                    Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy ID người dùng và gọi API
                SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                int userId = prefs.getInt("USER_ID", -1);

                if (userId != -1) {
                    changePasswordAPI(userId, oldPass, newPass);
                } else {
                    Toast.makeText(this, "Lỗi xác thực: Người dùng chưa đăng nhập!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void changePasswordAPI(int userId, String oldPassword, String newPassword) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("OldPassword", oldPassword);
        requestBody.put("NewPassword", newPassword);

        apiService.changePassword(userId, requestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    int statusCode = response.code();
                    if (statusCode == 400) {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu cũ không chính xác!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Đổi thất bại! Mã lỗi: " + statusCode, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối máy chủ!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}