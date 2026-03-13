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

// ... các code import giữ nguyên

public class ProfileActivity extends AppCompatActivity {

    // 1. Đổi tên biến ở đây
    private TextInputEditText edtUsername, edtFullName, edtEmail;
    private Button btnSave;

    private ApiService apiService;
    private String userToken = "Bearer TOKENCUABAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 2. Ánh xạ lại theo ID mới
        edtUsername = findViewById(R.id.edtUsername);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);
        ImageView btnBack = findViewById(R.id.btnBack);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        loadUserData();

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        apiService.getProfile(userToken).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body();

                    // 3. Gán dữ liệu mới lên màn hình
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
        // 4. Lấy dữ liệu mới người dùng vừa gõ
        String newUsername = edtUsername.getText().toString().trim();
        String newFullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (newUsername.isEmpty() || newFullName.isEmpty()) {
            Toast.makeText(this, "Vui lòng không để trống thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo object chứa dữ liệu mới để gửi lên C#
        UserProfile updatedUser = new UserProfile(newUsername, newFullName, email);

        apiService.updateProfile(userToken, updatedUser).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
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