package com.example.arfurnitureshop.activities; // Sửa lại đúng tên package của bạn

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.arfurnitureshop.R; // Sửa lại đúng tên package

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ View
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // Xử lý sự kiện nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Sau này sẽ gọi API kiểm tra đăng nhập ở đây
                // Tạm thời giả lập đăng nhập thành công và chuyển sang Trang chủ (MainActivity)
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Đóng màn hình Login
            }
        });

        // Xử lý sự kiện bấm vào "Đăng ký ngay"
        tvRegister.setOnClickListener(v -> {
            // Chuyển sang màn hình Đăng ký
            // Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            // startActivity(intent);
            Toast.makeText(LoginActivity.this, "Sẽ chuyển sang màn hình Đăng ký", Toast.LENGTH_SHORT).show();
        });
    }
}