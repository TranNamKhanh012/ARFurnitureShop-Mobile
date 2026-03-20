package com.example.arfurnitureshop.api;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // 1. THAY LINK THẬT CỦA BẠN VÀO ĐÂY
    private static final String BASE_URL = "http://trannamkhanh-001-site1.jtempurl.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 2. TẠO CHÌA KHÓA VƯỢT RÀO (BASIC AUTH)
            // Thay "user_cam" và "pass_cam" bằng thông tin ở bảng màu cam trên SmarterASP nhé
            String authToken = Credentials.basic("Tên_Người_Dùng_Cam", "Mật_Khẩu_Cam");

            // 3. THIẾT LẬP OKHTTP ĐỂ TỰ ĐỘNG GỬI CHÌA KHÓA TRONG MỖI REQUEST
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", authToken) // Gắn thẻ căn cước vào đây
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            // 4. KHỞI TẠO RETROFIT VỚI CLIENT ĐÃ CÓ CHÌA KHÓA
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}