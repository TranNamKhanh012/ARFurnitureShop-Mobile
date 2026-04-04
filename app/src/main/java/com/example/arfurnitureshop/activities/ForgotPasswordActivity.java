package com.example.arfurnitureshop.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo API
        apiService = RetrofitClient.getClient().create(ApiService.class);

        EditText etEmail = findViewById(R.id.etForgotEmail);
        Button btnReset = findViewById(R.id.btnResetPassword);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Nút quay lại
        tvBackToLogin.setOnClickListener(v -> finish());

        // Nút Gửi yêu cầu
        btnReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.toLowerCase().endsWith("@gmail.com")) {
                Toast.makeText(this, "Vui lòng nhập đúng định dạng @gmail.com!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hiện thông báo đang xử lý
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Đang kiểm tra hệ thống...");
            pd.show();

            // Gói dữ liệu Email gửi lên Server
            HashMap<String, String> body = new HashMap<>();
            body.put("email", email);

            // GỌI API THỰC TẾ
            apiService.forgotPassword(body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    pd.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        // Thành công: Trả về thông báo mật khẩu mới
                        String successMsg = response.body().get("message").getAsString();

                        // Hiển thị một bảng thông báo cho người dùng dễ đọc
                        new androidx.appcompat.app.AlertDialog.Builder(ForgotPasswordActivity.this)
                                .setTitle("Thành công!")
                                .setMessage(successMsg)
                                .setPositiveButton("Đã hiểu", (dialog, which) -> {
                                    finish(); // Đóng trang, quay về trang đăng nhập
                                })
                                .setCancelable(false)
                                .show();

                    } else {
                        // Thất bại (Email không tồn tại)
                        try {
                            String errorMsg = response.errorBody() != null ?
                                    new org.json.JSONObject(response.errorBody().string()).getString("message") :
                                    "Lỗi không xác định!";
                            Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(ForgotPasswordActivity.this, "Email này chưa được đăng ký!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối máy chủ!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}