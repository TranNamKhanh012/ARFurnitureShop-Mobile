package com.example.arfurnitureshop.api;

import com.example.arfurnitureshop.models.Category;
import com.example.arfurnitureshop.models.Product;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    // Gọi đến đường dẫn: http://.../api/Categories
    @GET("api/Categories")
    Call<List<Category>> getCategories();
    @GET("api/Products")
    Call<List<Product>> getProducts();
}