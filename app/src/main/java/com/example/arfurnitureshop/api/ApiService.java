package com.example.arfurnitureshop.api;

import com.example.arfurnitureshop.models.CartItem;
import com.example.arfurnitureshop.models.Category;
import com.example.arfurnitureshop.models.Product;
import com.example.arfurnitureshop.models.UserProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ==========================================
    // 1. KHỞI TẠO RETROFIT (Biến apiService để các nơi khác gọi)
    // ==========================================
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();


    // ==========================================
    // 2. CÁC ĐƯỜNG DẪN API (Của bạn giữ nguyên)
    // ==========================================

    @GET("api/Categories")
    Call<List<Category>> getCategories();

    @GET("api/Products")
    Call<List<Product>> getProducts();

    @GET("api/Products/category/{id}")
    Call<List<Product>> getProductsByCategory(@Path("id") int categoryId);

    @GET("api/Wishlist/{userId}")
    Call<List<Product>> getWishlist(@Path("userId") int userId);

    @POST("api/Wishlist/{userId}/{productId}")
    Call<Void> addToWishlist(@Path("userId") int userId, @Path("productId") int productId);

    @DELETE("api/Wishlist/{userId}/{productId}")
    Call<Void> removeFromWishlist(@Path("userId") int userId, @Path("productId") int productId);

    // Lấy giỏ hàng phải dùng List<CartItem> (để hứng được cả Số lượng)
    @GET("api/Cart/{userId}")
    Call<List<com.example.arfurnitureshop.models.CartItem>> getCart(@Path("userId") int userId);
    // =========================================
    // 1. THÊM VÀO GIỎ HÀNG (Kèm Số lượng & Size)
    // =========================================
    @POST("api/Cart/add")
    Call<Void> addToCart(
            @Query("userId") int userId,
            @Query("productId") int productId,
            @Query("quantity") int quantity,
            @Query("selectedSize") String selectedSize
    );

    // =========================================
    // 2. XÓA KHỎI GIỎ HÀNG (Kèm Size để xóa cho đúng)
    // =========================================
    @DELETE("api/Cart/remove")
    Call<Void> removeFromCart(
            @Query("userId") int userId,
            @Query("productId") int productId,
            @Query("selectedSize") String selectedSize
    );
    // API Authentication
    @POST("api/Auth/login")
    Call<com.example.arfurnitureshop.models.User> login(@retrofit2.http.Body com.example.arfurnitureshop.models.User user);

    @POST("api/Auth/register")
    Call<com.google.gson.JsonObject> register(@retrofit2.http.Body com.example.arfurnitureshop.models.User user);
    // Thay vì gửi nguyên cục User, giờ ta chỉ gửi LoginRequest lên
    @POST("api/Auth/login")
    Call<com.example.arfurnitureshop.models.User> login(@retrofit2.http.Body com.example.arfurnitureshop.models.LoginRequest request);


    // Lấy thông tin user bằng ID
    @GET("api/Auth/profile/{id}")
    Call<UserProfile> getProfile(@Path("id") int userId);

    // Cập nhật thông tin user bằng ID
    @PUT("api/Auth/update/{id}")
    Call<UserProfile> updateProfile(@Path("id") int userId, @Body UserProfile user);
    // Lệnh gọi API xóa tài khoản (truyền ID người dùng vào)
    // Sửa đường dẫn từ api/users/{id} thành api/auth/{id}
    @DELETE("api/auth/{id}")
    Call<Void> deleteAccount(@Path("id") int userId);
    // Gọi API Đổi mật khẩu
    @PUT("api/auth/change-password/{id}")
    Call<Void> changePassword(@Path("id") int userId, @Body java.util.HashMap<String, String> body);

    // Lệnh gọi API tìm kiếm
    @GET("api/products/search") // Sửa lại chữ 'products' cho đúng tên Controller C# của bạn
    Call<List<Product>> searchProducts(@Query("keyword") String keyword);
    // ==========================================
    // TÌM KIẾM, LỌC VÀ SẮP XẾP NÂNG CAO
    // ==========================================
    @GET("api/products/filter-sort")
    Call<List<Product>> getFilteredSortedProducts(
            @Query("query") String query,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("sortBy") String sortBy
    );

    // ==========================================
    // API QUẢN LÝ ĐỊA CHỈ GIAO HÀNG
    // ==========================================
    @GET("api/Addresses/user/{userId}")
    Call<List<com.example.arfurnitureshop.models.UserAddress>> getUserAddresses(@Path("userId") int userId);

    @POST("api/Addresses")
    Call<com.example.arfurnitureshop.models.UserAddress> addAddress(@Body com.example.arfurnitureshop.models.UserAddress address);

    @PUT("api/Addresses/{id}")
    Call<Void> updateAddress(@Path("id") int id, @Body com.example.arfurnitureshop.models.UserAddress address);

    @DELETE("api/Addresses/{id}")
    Call<Void> deleteAddress(@Path("id") int id);
    // Thay đổi từ Call<String> thành Call<okhttp3.ResponseBody>
    @GET("api/Payment/get-vnpay-url")
    Call<okhttp3.ResponseBody> getVnpayUrl(@Query("amount") double amount);
    // THÊM ĐƯỜNG DẪN CHÍNH XÁC CỦA MODEL VÀO TRƯỚC TÊN BIẾN
    @POST("api/Orders/create")
    Call<okhttp3.ResponseBody> createOrder(@Body com.example.arfurnitureshop.models.OrderRequestDto orderDto);
    // Lấy danh sách địa chỉ của User

    // Lấy lịch sử mua hàng
    // Lấy lịch sử mua hàng
    @GET("api/Orders/user/{userId}")
    Call<java.util.List<com.example.arfurnitureshop.models.Order>> getUserOrders(@Path("userId") int userId);

    // Gọi chung API của Admin để lấy chi tiết đơn hàng
    @GET("api/Orders/admin-get/{id}")
    Call<com.example.arfurnitureshop.models.OrderDetailResponse> getOrderDetail(@Path("id") int orderId);
    // Gọi API kiểm tra mặt hàng chờ đánh giá
    @GET("api/Reviews/pending/{userId}")
    Call<java.util.List<com.example.arfurnitureshop.models.Product>> getPendingReviews(@Path("userId") int userId);
    // Khách hàng xác nhận đã nhận hàng
    @PUT("api/Orders/user-confirm/{id}")
    Call<okhttp3.ResponseBody> confirmOrderReceived(@Path("id") int orderId);

    // Gửi đánh giá mới
    @POST("api/Reviews/create")
    Call<okhttp3.ResponseBody> createReview(@Body com.example.arfurnitureshop.models.ReviewRequest reviewRequest);

    // Lấy danh sách đánh giá của 1 sản phẩm
    @GET("api/Reviews/product/{productId}")
    Call<java.util.List<com.example.arfurnitureshop.models.ReviewResponse>> getProductReviews(@Path("productId") int productId);

}