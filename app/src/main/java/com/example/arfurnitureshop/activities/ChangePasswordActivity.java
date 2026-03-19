package com.example.arfurnitureshop.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

        ImageView ivBack = findViewById(R.id.ivBack);
        EditText edtOldPassword = findViewById(R.id.edtOldPassword);
        EditText edtNewPassword = findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        Button btnSavePassword = findViewById(R.id.btnSavePassword);

        // Đóng trang
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

        // Xử lý khi bấm nút Cập nhật
        if (btnSavePassword != null) {
            btnSavePassword.setOnClickListener(v -> {
                String oldPass = edtOldPassword.getText().toString().trim();
                String newPass = edtNewPassword.getText().toString().trim();
                String confirmPass = edtConfirmPassword.getText().toString().trim();

                // 1. Kiểm tra nhập liệu trống
                if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2. Kiểm tra mật khẩu mới và xác nhận phải giống nhau
                if (!newPass.equals(confirmPass)) {
                    Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 3. Lấy ID người dùng và gọi API
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

        // ĐÃ SỬA LỖI 1: Viết hoa chữ cái đầu cho khớp đúng 100% với Class ChangePasswordDto bên C#
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("OldPassword", oldPassword);
        requestBody.put("NewPassword", newPassword);

        apiService.changePassword(userId, requestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                    finish(); // Thành công thì tự động đóng trang
                } else {
                    // ĐÃ SỬA LỖI 2: In ra mã lỗi chi tiết để bắt đúng bệnh
                    int statusCode = response.code();

                    if (statusCode == 400) {
                        Toast.makeText(ChangePasswordActivity.this, "Mật khẩu cũ không chính xác!", Toast.LENGTH_SHORT).show();
                    }
                    else if (statusCode == 404) {
                        Toast.makeText(ChangePasswordActivity.this, "Lỗi 404: Không tìm thấy tài khoản (Hoặc sai đường dẫn API)!", Toast.LENGTH_LONG).show();
                    }
                    else if (statusCode == 415) {
                        Toast.makeText(ChangePasswordActivity.this, "Lỗi 415: C# không hiểu định dạng dữ liệu gửi lên!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(ChangePasswordActivity.this, "Đổi thất bại! Mã lỗi Server: " + statusCode, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối máy chủ C#!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}