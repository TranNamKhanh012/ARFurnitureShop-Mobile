package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
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
        ImageView btnBack = findViewById(R.id.btnBack);

        // Khởi tạo Retrofit
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Lấy dữ liệu khi vừa mở màn hình
        loadUserData();

        // Xử lý nút bấm
        btnBack.setOnClickListener(v -> finish());
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