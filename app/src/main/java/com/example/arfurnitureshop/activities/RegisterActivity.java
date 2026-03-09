package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

            ApiService.apiService.register(newUser).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        // ====================================================
                        // BẮT ANDROID ĐỌC XEM C# BÁO LỖI GÌ
                        try {
                            String loiCuaCSharp = response.errorBody().string();
                            Toast.makeText(RegisterActivity.this, "C# nói là: " + loiCuaCSharp, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "Lỗi Server rồi!", Toast.LENGTH_SHORT).show();
                        }
                        // ====================================================
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}