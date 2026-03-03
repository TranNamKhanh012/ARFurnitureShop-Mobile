package com.example.arfurnitureshop.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory; // PHẢI LÀ DÒNG NÀY

public class RetrofitClient {
    // Nhớ dùng đúng cổng (port) mà Visual Studio đang chạy (ví dụ 5003 hoặc 7003)
    private static final String BASE_URL = "http://10.0.2.2:5103/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Dùng đúng tên ở đây
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}