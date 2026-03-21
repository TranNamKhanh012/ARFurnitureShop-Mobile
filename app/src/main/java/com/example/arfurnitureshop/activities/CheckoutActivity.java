package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import java.text.NumberFormat;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private TextView tvTotal;
    private Button btnPay;
    private ApiService apiService;
    private double totalAmount = 0; // Để mặc định là 0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        tvTotal = findViewById(R.id.tvTotalCheckout);
        btnPay = findViewById(R.id.btnPayVNPAY);

        // 1. NHẬN SỐ TIỀN THỰC TẾ (Từ Cart hoặc Buy Now gửi sang)
        totalAmount = getIntent().getDoubleExtra("TOTAL_PRICE", 0);

        // 2. Định dạng tiền tệ VND cho đẹp
        NumberFormat formatVN = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotal.setText("Tổng cộng: " + formatVN.format(totalAmount));

        apiService = RetrofitClient.getClient().create(ApiService.class);

        btnPay.setOnClickListener(v -> {
            // Hiển thị thông báo đang xử lý
            Toast.makeText(this, "Đang khởi tạo cổng thanh toán...", Toast.LENGTH_SHORT).show();

            apiService.getVnpayUrl(totalAmount).enqueue(new Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            // Lấy chuỗi link VNPAY thực sự từ ResponseBody
                            String url = response.body().string().replace("\"", "");

                            // Mở WebView
                            Intent intent = new Intent(CheckoutActivity.this, PaymentWebViewActivity.class);
                            intent.putExtra("VNPAY_URL", url);
                            startActivity(intent);

                        } catch (Exception e) {
                            Log.e("VNPAY_ERROR", "Lỗi đọc chuỗi link: " + e.getMessage());
                        }
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Server không trả về link!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                    // Lỗi này thường do cấu hình Retrofit không khớp (JSON vs String)
                    Log.e("VNPAY_ERROR", "Lỗi kết nối: " + t.getMessage());
                    Toast.makeText(CheckoutActivity.this, "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}