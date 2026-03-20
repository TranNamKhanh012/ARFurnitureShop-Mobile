package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.UserAddress;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    private TextInputEditText edtReceiverName, edtPhoneNumber, edtFullAddress;
    private SwitchCompat switchDefault;
    private Button btnSaveAddress;

    private ApiService apiService;
    private int currentUserId;

    // Biến lưu trạng thái xem là đang Thêm mới hay Sửa
    private boolean isEditMode = false;
    private int editAddressId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        ImageView ivBack = findViewById(R.id.ivBack);
        edtReceiverName = findViewById(R.id.edtReceiverName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtFullAddress = findViewById(R.id.edtFullAddress);
        switchDefault = findViewById(R.id.switchDefault);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        currentUserId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("USER_ID", -1);

        // ===============================================
        // KIỂM TRA XEM CÓ PHẢI LÀ ĐANG BẤM TỪ NÚT "SỬA" SANG KHÔNG
        // ===============================================
        isEditMode = getIntent().getBooleanExtra("IS_EDIT_MODE", false);
        if (isEditMode) {
            editAddressId = getIntent().getIntExtra("ADDRESS_ID", -1);

            // Tự động điền dữ liệu cũ vào Form
            edtReceiverName.setText(getIntent().getStringExtra("RECEIVER_NAME"));
            edtPhoneNumber.setText(getIntent().getStringExtra("PHONE_NUMBER"));
            edtFullAddress.setText(getIntent().getStringExtra("FULL_ADDRESS"));
            switchDefault.setChecked(getIntent().getBooleanExtra("IS_DEFAULT", false));

            // Đổi chữ trên Nút bấm
            btnSaveAddress.setText("CẬP NHẬT ĐỊA CHỈ");
        } else {
            // NẾU LÀ THÊM MỚI -> Tự điền tên Profile vào
            String currentName = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("FULL_NAME", "");
            edtReceiverName.setText(currentName);
        }

        ivBack.setOnClickListener(v -> finish());
        btnSaveAddress.setOnClickListener(v -> saveAddressToServer());
    }

    private void saveAddressToServer() {
        if (currentUserId == -1) return;

        String name = edtReceiverName.getText().toString().trim();
        String phone = edtPhoneNumber.getText().toString().trim();
        String address = edtFullAddress.getText().toString().trim();
        boolean isDefault = switchDefault.isChecked();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            // ==========================================
            // GỌI API SỬA (PUT)
            // ==========================================
            UserAddress updatedAddress = new UserAddress(editAddressId, currentUserId, name, phone, address, isDefault);
            apiService.updateAddress(editAddressId, updatedAddress).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddAddressActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {}
            });
        } else {
            // ==========================================
            // GỌI API THÊM MỚI (POST)
            // ==========================================
            UserAddress newAddress = new UserAddress(currentUserId, name, phone, address, isDefault);
            apiService.addAddress(newAddress).enqueue(new Callback<UserAddress>() {
                @Override
                public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddAddressActivity.this, "Thêm địa chỉ thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<UserAddress> call, Throwable t) {}
            });
        }
    }
}