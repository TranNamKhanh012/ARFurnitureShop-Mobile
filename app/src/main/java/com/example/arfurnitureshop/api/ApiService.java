package com.example.arfurnitureshop.api;

import com.example.arfurnitureshop.models.Category;
import com.example.arfurnitureshop.models.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // ==========================================
    // 1. KHỞI TẠO RETROFIT (Biến apiService để các nơi khác gọi)
    // ==========================================
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    // LƯU Ý QUAN TRỌNG: Bạn nhớ thay cái baseUrl bằng link API thật của bạn nhé!
    // Ví dụ đang chạy C# ở máy ảo: http://10.0.2.2:5000/
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.176:5103/") // <-- SỬA PORT Ở ĐÂY CHO ĐÚNG VỚI VISUAL STUDIO CỦA BẠN
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

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

    @GET("api/Cart/{userId}")
    Call<List<Product>> getCart(@Path("userId") int userId);
    @POST("api/Cart/{userId}/{productId}")
    Call<Void> addToCart(@Path("userId") int userId, @Path("productId") int productId);

    @DELETE("api/Cart/{userId}/{productId}")
    Call<Void> removeFromCart(@Path("userId") int userId, @Path("productId") int productId);

    // API Authentication
    @POST("api/Auth/login")
    Call<com.example.arfurnitureshop.models.User> login(@retrofit2.http.Body com.example.arfurnitureshop.models.User user);

    @POST("api/Auth/register")
    Call<Void> register(@retrofit2.http.Body com.example.arfurnitureshop.models.User user);

    // Thay vì gửi nguyên cục User, giờ ta chỉ gửi LoginRequest lên
    @POST("api/Auth/login")
    Call<com.example.arfurnitureshop.models.User> login(@retrofit2.http.Body com.example.arfurnitureshop.models.LoginRequest request);


}