package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.adapters.CartAdapter;
import com.example.arfurnitureshop.api.ApiService;
import com.example.arfurnitureshop.api.RetrofitClient;
import com.example.arfurnitureshop.models.CartItem;
import com.example.arfurnitureshop.models.OrderRequestDto;
import com.example.arfurnitureshop.models.UserAddress;
import com.example.arfurnitureshop.utils.CartManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    // Các View mới cho phần Địa chỉ
    private TextView tvReceiverNamePhone, tvFullAddress, tvNoAddress, btnChangeAddress;
    private LinearLayout layoutAddressInfo;

    // Các View khác
    private TextView tvTotal;
    private RadioGroup rgPayment;
    private Button btnPlaceOrder;
    private RecyclerView rvSummary;

    private double totalAmount = 0;
    private ApiService apiService;
    private List<CartItem> cartItems;

    // Biến lưu địa chỉ đang được chọn để đẩy lên Server
    private UserAddress selectedAddress = null;

    // TRÌNH LẮNG NGHE: Đợi kết quả chọn địa chỉ trả về từ trang MyAddressesActivity
    private final ActivityResultLauncher<Intent> addressPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    UserAddress chosenAddress = (UserAddress) result.getData().getSerializableExtra("SELECTED_ADDRESS");
                    if (chosenAddress != null) {
                        updateAddressUI(chosenAddress); // Cập nhật lại giao diện ngay lập tức
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // ==========================================
        // 1. ÁNH XẠ TOÀN BỘ VIEW
        // ==========================================
        ImageView ivBack = findViewById(R.id.ivBackCheckout);
        tvReceiverNamePhone = findViewById(R.id.tvReceiverNamePhone);
        tvFullAddress = findViewById(R.id.tvFullAddress);
        tvNoAddress = findViewById(R.id.tvNoAddress);
        btnChangeAddress = findViewById(R.id.btnChangeAddress);
        layoutAddressInfo = findViewById(R.id.layoutAddressInfo);

        rgPayment = findViewById(R.id.rgPaymentMethod);
        tvTotal = findViewById(R.id.tvTotalCheckout);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        rvSummary = findViewById(R.id.rvOrderSummary);

        // Khởi tạo API
        apiService = RetrofitClient.getClient().create(ApiService.class);
        ivBack.setOnClickListener(v -> finish());

        // ==========================================
        // 2. NẠP DỮ LIỆU ĐƠN HÀNG (SỐ TIỀN & SẢN PHẨM)
        // ==========================================
        totalAmount = getIntent().getDoubleExtra("TOTAL_PRICE", 0);
        DecimalFormat df = new DecimalFormat("#,###");
        tvTotal.setText("₫ " + df.format(totalAmount) + " VND");

        cartItems = CartManager.getInstance(this).getItems();
        rvSummary.setLayoutManager(new LinearLayoutManager(this));

        CartAdapter summaryAdapter = new CartAdapter(cartItems, null);
        rvSummary.setAdapter(summaryAdapter);


        // ==========================================
        // 3. TỰ ĐỘNG LẤY ĐỊA CHỈ & XỬ LÝ NÚT THAY ĐỔI
        // ==========================================
        // Gọi API tải địa chỉ khi vừa vào trang
        loadUserAddresses();

        // Nút "THAY ĐỔI >" mở trang danh sách địa chỉ
        btnChangeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, MyAddressesActivity.class);
            intent.putExtra("IS_SELECTION_MODE", true); // Bật cờ "Đang chọn địa chỉ"
            addressPickerLauncher.launch(intent);
        });


        // ==========================================
        // 4. XỬ LÝ NÚT ĐẶT HÀNG (PLACE ORDER)
        // ==========================================
        btnPlaceOrder.setOnClickListener(v -> {

            // 1. Kiểm tra xem đã có địa chỉ giao hàng chưa
            if (selectedAddress == null) {
                Toast.makeText(this, "Vui lòng thêm hoặc chọn địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Xác định phương thức thanh toán
            int checkedId = rgPayment.getCheckedRadioButtonId();
            String paymentMethod = (checkedId == R.id.rbMock) ? "MOCK" : "COD";

            // 3. Hiện Progess chờ
            android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
            pd.setMessage("Đang xử lý đơn hàng...");
            pd.show();

            // 4. ĐÓNG GÓI DỮ LIỆU (Đẩy Database)
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = prefs.getInt("USER_ID", -1);

            OrderRequestDto orderDto = new OrderRequestDto();
            orderDto.userId = userId;
            orderDto.totalAmount = totalAmount;
            orderDto.paymentMethod = paymentMethod;

            // Lấy thông tin từ địa chỉ được chọn
            orderDto.shippingAddress = selectedAddress.getFullAddress();
            orderDto.phoneNumber = selectedAddress.getPhoneNumber();
            orderDto.receiverName = selectedAddress.getReceiverName();

            orderDto.items = new ArrayList<>();

            // Chuyển CartItem -> OrderItemDto
            for (CartItem cartItem : cartItems) {
                OrderRequestDto.OrderItemDto itemDto = new OrderRequestDto.OrderItemDto(
                        cartItem.getProduct().getId(),
                        cartItem.getQuantity(),
                        cartItem.getProduct().getPrice()
                );
                orderDto.items.add(itemDto);
            }

            // 5. GỌI API ĐẨY ĐƠN HÀNG LÊN DATABASE C#
            apiService.createOrder(orderDto).enqueue(new Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                    pd.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(CheckoutActivity.this, "🎉 Đặt hàng thành công!", Toast.LENGTH_LONG).show();

                        // Xóa sạch giỏ hàng
                        CartManager.getInstance(CheckoutActivity.this).clear();

                        // Về Trang chủ
                        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Lỗi API lưu đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(CheckoutActivity.this, "Lỗi kết nối Local: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // ==========================================
    // CÁC HÀM HỖ TRỢ XỬ LÝ ĐỊA CHỈ
    // ==========================================
    private void loadUserAddresses() {
        int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("USER_ID", -1);
        if (userId == -1) return;

        apiService.getUserAddresses(userId).enqueue(new Callback<List<UserAddress>>() {
            @Override
            public void onResponse(Call<List<UserAddress>> call, Response<List<UserAddress>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<UserAddress> addresses = response.body();

                    // Ưu tiên tìm địa chỉ có isDefault = true
                    UserAddress defaultAddr = addresses.get(0);
                    for (UserAddress addr : addresses) {
                        if (addr.isDefault()) {
                            defaultAddr = addr;
                            break;
                        }
                    }
                    updateAddressUI(defaultAddr);
                } else {
                    // Không có địa chỉ nào trong DB
                    layoutAddressInfo.setVisibility(View.GONE);
                    tvNoAddress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<UserAddress>> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Không thể tải địa chỉ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAddressUI(UserAddress address) {
        selectedAddress = address;
        layoutAddressInfo.setVisibility(View.VISIBLE);
        tvNoAddress.setVisibility(View.GONE);

        tvReceiverNamePhone.setText(address.getReceiverName() + " | " + address.getPhoneNumber());
        tvFullAddress.setText(address.getFullAddress());
    }
}