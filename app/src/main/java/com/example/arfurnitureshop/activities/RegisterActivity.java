package com.example.arfurnitureshop.activities;

import android.app.ProgressDialog;
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
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo API Service 1 lần duy nhất
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

            // 1. Kiểm tra không được để trống
            if (user.isEmpty() || pass.isEmpty() || name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // ==========================================
            // 2. KIỂM TRA ĐIỀU KIỆN MẬT KHẨU VÀ EMAIL
            // ==========================================
            if (pass.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có tối thiểu 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.toLowerCase().endsWith("@gmail.com")) {
                Toast.makeText(this, "Vui lòng sử dụng địa chỉ @gmail.com!", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(user, pass, name, email);

            // Hiện thông báo đang xử lý
            ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
            pd.setMessage("Đang tạo tài khoản...");
            pd.show();

            // Gọi API
            apiService.register(newUser).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    pd.dismiss(); // Tắt thông báo đang xử lý

                    if (response.isSuccessful()) {
                        // Backend trả về mã 200 OK (Đăng ký thành công)
                        Toast.makeText(RegisterActivity.this, "🎉 Đăng ký thành công!", Toast.LENGTH_LONG).show();

                        // Đóng trang đăng ký, quay trở lại trang đăng nhập
                        finish();
                    } else {
                        // Backend trả về lỗi (Ví dụ: mã 400 - Trùng tên đăng nhập)
                        Toast.makeText(RegisterActivity.this, "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!", Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR", "Mã lỗi: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show();
                    Log.e("API_FAILURE", t.getMessage());
                }
            });
        });
    }
}