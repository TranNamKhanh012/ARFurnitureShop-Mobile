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
    private boolean isBuyNow;

    // TRÌNH LẮNG NGHE 1: Đợi kết quả chọn địa chỉ trả về từ trang MyAddressesActivity
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

    // ==========================================
    // [MỚI] TRÌNH LẮNG NGHE 2: Đợi kết quả từ cổng thanh toán VNPAY
    // ==========================================
    private final ActivityResultLauncher<Intent> vnpayLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Nếu VNPAY trả về OK -> Bắt đầu đẩy đơn hàng lên Database (Đã trả tiền)
                    submitOrderToServer("VNPAY");
                } else {
                    // Khách bấm nút Hủy hoặc tắt ngang trang web
                    Toast.makeText(this, "Bạn đã hủy thanh toán hoặc giao dịch bị lỗi.", Toast.LENGTH_SHORT).show();
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
        // 2. NẠP DỮ LIỆU ĐƠN HÀNG (SẢN PHẨM & TỰ TÍNH LẠI TIỀN)
        // ==========================================
        // Đã sửa lỗi bóng mờ biến (shadowing) bằng cách bỏ chữ 'boolean'
        isBuyNow = getIntent().getBooleanExtra("IS_BUY_NOW", false);

        if (isBuyNow) {
            // NẾU LÀ MUA NGAY: Tạo một giỏ hàng ảo chỉ chứa đúng 1 sản phẩm
            cartItems = new ArrayList<>();
            com.example.arfurnitureshop.models.Product p = new com.example.arfurnitureshop.models.Product();
            p.setId(getIntent().getIntExtra("BUY_NOW_ID", 0));
            p.setName(getIntent().getStringExtra("BUY_NOW_NAME"));
            p.setPrice(getIntent().getDoubleExtra("BUY_NOW_PRICE", 0));
            p.setImageUrl(getIntent().getStringExtra("BUY_NOW_IMAGE"));
            p.setDiscount(getIntent().getIntExtra("BUY_NOW_DISCOUNT", 0));

            int qty = getIntent().getIntExtra("BUY_NOW_QTY", 1);
            String size = getIntent().getStringExtra("BUY_NOW_SIZE");
            if (size == null) size = "";

            cartItems.add(new CartItem(p, qty, size));
        } else {
            // NẾU TỪ GIỎ HÀNG: Lấy dữ liệu từ SQLite
            cartItems = CartManager.getInstance(this).getItems();
        }

        totalAmount = 0;
        for (CartItem item : cartItems) {
            double finalUnitPrice = item.getProduct().getPrice();
            if (item.getProduct().getDiscount() > 0) {
                finalUnitPrice = finalUnitPrice - (finalUnitPrice * item.getProduct().getDiscount() / 100.0);
            }
            totalAmount += (finalUnitPrice * item.getQuantity());
        }

        DecimalFormat df = new DecimalFormat("#,###");
        tvTotal.setText("₫ " + df.format(totalAmount) + " VND");

        rvSummary.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter summaryAdapter = new CartAdapter(cartItems, null);
        rvSummary.setAdapter(summaryAdapter);

        // ==========================================
        // 3. TỰ ĐỘNG LẤY ĐỊA CHỈ & XỬ LÝ NÚT THAY ĐỔI
        // ==========================================
        loadUserAddresses();

        btnChangeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, MyAddressesActivity.class);
            intent.putExtra("IS_SELECTION_MODE", true);
            addressPickerLauncher.launch(intent);
        });

        // ==========================================
        // 4. XỬ LÝ NÚT ĐẶT HÀNG (PLACE ORDER) - LUỒNG VNPAY
        // ==========================================
        btnPlaceOrder.setOnClickListener(v -> {

            if (selectedAddress == null) {
                Toast.makeText(this, "Vui lòng thêm hoặc chọn địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
                return;
            }

            int checkedId = rgPayment.getCheckedRadioButtonId();
            // Đã đổi chữ MOCK thành VNPAY cho chuẩn
            String paymentMethod = (checkedId == R.id.rbMock) ? "VNPAY" : "COD";

            if (paymentMethod.equals("VNPAY")) {
                // NẾU CHỌN THẺ -> GỌI API LẤY LINK VNPAY RỒI MỞ TRANG WEB
                android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
                pd.setMessage("Đang kết nối cổng thanh toán VNPAY...");
                pd.show();

                apiService.getVnpayUrl(totalAmount).enqueue(new Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                        pd.dismiss();
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                String url = response.body().string(); // Lấy link VNPAY
                                Intent intent = new Intent(CheckoutActivity.this, PaymentWebViewActivity.class);
                                intent.putExtra("VNPAY_URL", url);
                                vnpayLauncher.launch(intent); // Mở Webview chờ khách trả tiền
                            } else {
                                Toast.makeText(CheckoutActivity.this, "Lỗi tạo link thanh toán", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(CheckoutActivity.this, "Lỗi kết nối VNPAY", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // NẾU LÀ COD -> ĐẨY ĐƠN HÀNG LÊN SERVER LUÔN (Không cần mở web)
                submitOrderToServer("COD");
            }
        });
    }

    // ==========================================
    // [MỚI] HÀM TẠO ĐƠN HÀNG VÀ ĐẨY LÊN SQL SERVER (Dùng chung cho cả COD và VNPAY)
    // ==========================================
    private void submitOrderToServer(String paymentMethod) {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(this);
        pd.setMessage("Đang xử lý đơn hàng...");
        pd.show();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", -1);

        OrderRequestDto orderDto = new OrderRequestDto();
        orderDto.userId = userId;
        orderDto.totalAmount = totalAmount;
        orderDto.paymentMethod = paymentMethod;
        orderDto.shippingAddress = selectedAddress.getFullAddress();
        orderDto.phoneNumber = selectedAddress.getPhoneNumber();
        orderDto.receiverName = selectedAddress.getReceiverName();

        orderDto.items = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            double finalUnitPrice = cartItem.getProduct().getPrice();
            if (cartItem.getProduct().getDiscount() > 0) {
                finalUnitPrice = finalUnitPrice - (finalUnitPrice * cartItem.getProduct().getDiscount() / 100.0);
            }
            OrderRequestDto.OrderItemDto itemDto = new OrderRequestDto.OrderItemDto(
                    cartItem.getProduct().getId(),
                    cartItem.getQuantity(),
                    finalUnitPrice
            );
            itemDto.selectedSize = cartItem.getSelectedSize() != null ? cartItem.getSelectedSize() : "";
            orderDto.items.add(itemDto);
        }

        apiService.createOrder(orderDto).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                pd.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(CheckoutActivity.this, "🎉 Đặt hàng thành công!", Toast.LENGTH_LONG).show();

                    if (!isBuyNow) {
                        CartManager.getInstance(CheckoutActivity.this).clear();
                    }
                    // Cập nhật lại số lượng giỏ hàng ở thanh Bottom Nav
                    com.example.arfurnitureshop.utils.BadgeUtils.fetchAndCacheBadges(CheckoutActivity.this);

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

                    UserAddress defaultAddr = addresses.get(0);
                    for (UserAddress addr : addresses) {
                        if (addr.isDefault()) {
                            defaultAddr = addr;
                            break;
                        }
                    }
                    updateAddressUI(defaultAddr);
                } else {
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