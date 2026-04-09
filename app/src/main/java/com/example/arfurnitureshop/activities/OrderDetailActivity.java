package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.OrderProductAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.OrderDetailResponse;
import java.text.DecimalFormat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView tvName, tvPhone, tvAddress, tvTotal, tvMethod, tvStatus;
    private RecyclerView rvProducts;
    private android.widget.Button btnConfirmReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        ImageView ivBack = findViewById(R.id.btnBack);
        ivBack.setOnClickListener(v -> finish());

        // (Nếu bạn muốn đổi luôn Tiêu đề trang cho đúng, hãy ánh xạ thêm tvHeaderTitle)
        TextView tvTitle = findViewById(R.id.tvHeaderTitle);
        if (tvTitle != null) {
            tvTitle.setText("Chi tiết đơn hàng");
        }
        ivBack.setOnClickListener(v -> finish());

        tvName = findViewById(R.id.tvInfoName);
        tvPhone = findViewById(R.id.tvInfoPhone);
        tvAddress = findViewById(R.id.tvInfoAddress);
        tvTotal = findViewById(R.id.tvInfoTotal);
        tvMethod = findViewById(R.id.tvInfoMethod);
        tvStatus = findViewById(R.id.tvInfoStatus);

        rvProducts = findViewById(R.id.rvOrderProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        btnConfirmReceived = findViewById(R.id.btnConfirmReceived);

        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId != -1) {
            loadOrderDetail(orderId);
        }
    }

    private void loadOrderDetail(int id) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getOrderDetail(id).enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, Response<OrderDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderDetailResponse order = response.body();
                    DecimalFormat df = new DecimalFormat("#,###");

                    tvName.setText(order.getReceiverName());
                    tvPhone.setText(order.getPhoneNumber());
                    tvAddress.setText(order.getShippingAddress());
                    tvTotal.setText("₫ " + df.format(order.getTotalAmount()));
                    tvMethod.setText("Phương thức: " + order.getPaymentMethod());

                    String statusText = order.getOrderStatus().equals("Pending") ? "Đang chờ xác nhận" : order.getOrderStatus();
                    tvStatus.setText("Trạng thái: " + statusText);

                    if ("Shipping".equalsIgnoreCase(order.getOrderStatus())) {
                        btnConfirmReceived.setVisibility(android.view.View.VISIBLE);

                        btnConfirmReceived.setOnClickListener(v -> {
                            // Hiện hộp thoại xác nhận cho chắc chắn
                            new androidx.appcompat.app.AlertDialog.Builder(OrderDetailActivity.this)
                                    .setTitle("Xác nhận")
                                    .setMessage("Bạn xác nhận đã nhận được hàng nguyên vẹn và đúng sản phẩm?")
                                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                                        // Gọi API xác nhận
                                        apiService.confirmOrderReceived(id).enqueue(new Callback<okhttp3.ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(OrderDetailActivity.this, "Cảm ơn bạn! Hãy đánh giá sản phẩm nhé.", Toast.LENGTH_LONG).show();
                                                    // Ẩn nút đi và đổi chữ trạng thái
                                                    btnConfirmReceived.setVisibility(android.view.View.GONE);
                                                    tvStatus.setText("Trạng thái: Completed");
                                                } else {
                                                    try {
                                                        String errorMsg = response.errorBody().string();
                                                        Toast.makeText(OrderDetailActivity.this, "Lỗi " + response.code() + ": " + errorMsg, Toast.LENGTH_LONG).show();
                                                    } catch (Exception e) {
                                                        Toast.makeText(OrderDetailActivity.this, "Mã lỗi: " + response.code(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {}
                                        });
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                        });
                    } else {
                        btnConfirmReceived.setVisibility(android.view.View.GONE);
                    }

                    // Hiển thị danh sách sản phẩm
                    OrderProductAdapter adapter = new OrderProductAdapter(order.getItems());
                    rvProducts.setAdapter(adapter);
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Lỗi lấy chi tiết đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}