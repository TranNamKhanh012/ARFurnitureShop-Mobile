package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        EditText etUser = findViewById(R.id.etRegUsername);
        EditText etPass = findViewById(R.id.etRegPassword);
        EditText etName = findViewById(R.id.etRegFullName);
        EditText etEmail = findViewById(R.id.etRegEmail);
        Button btnReg = findViewById(R.id.btnRegister);

        btnReg.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Tài khoản và Mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(user, pass, name, email);

            // Khởi tạo ở onCreate
            apiService = RetrofitClient.getClient().create(ApiService.class);

// Khi bấm nút Register
            apiService.register(newUser).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // CHỈ KHI VÀO ĐÂY THÌ DỮ LIỆU MỚI CÓ TRÊN DATABASE
                        Toast.makeText(RegisterActivity.this, "Đã lưu lên Cloud!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lỗi từ phía Server (ví dụ: trùng Username)
                        Log.e("API_ERROR", "Mã lỗi: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Lỗi kết nối (Mất mạng hoặc sai địa chỉ IP/Domain)
                    Log.e("API_FAILURE", t.getMessage());
                }
            });
        });
    }
}