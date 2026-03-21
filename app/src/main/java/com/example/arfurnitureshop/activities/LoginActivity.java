package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.LoginRequest;
import com.example.arfurnitureshop.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        EditText etUser = findViewById(R.id.etLoginUsername);
        EditText etPass = findViewById(R.id.etLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Chuyển sang trang Đăng ký
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Xử lý Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = etUser.getText().toString().trim();
            String password = etPass.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng gửi đi
            LoginRequest loginRequest = new LoginRequest(username, password);

            // Gọi API
            // Thay vì gọi ApiService.apiService..., hãy dùng biến apiService đã khởi tạo ở onCreate
            apiService.login(loginRequest).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User loggedInUser = response.body();

                        // Lưu thông tin vào SharedPreferences (Giữ nguyên logic của bạn)
                        android.content.SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE);
                        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("IS_LOGGED_IN", true);
                        editor.putInt("USER_ID", loggedInUser.getId());
                        editor.putString("USERNAME", loggedInUser.getUsername());
                        editor.putString("FULL_NAME", loggedInUser.getFullName());
                        editor.putString("ROLE", loggedInUser.getRole());
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Xin chào " + loggedInUser.getFullName(), Toast.LENGTH_SHORT).show();

                        // Chuyển trang dựa trên Role
                        if ("Admin".equals(loggedInUser.getRole())) {
                            Toast.makeText(LoginActivity.this, "Tài khoản Admin vui lòng đăng nhập trên Web!", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Nếu server trả về lỗi (sai pass hoặc sai user mây)
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    // Lỗi này thường do: Quên bật usesCleartextTraffic hoặc sai Username/Password màu cam trong RetrofitClient
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}