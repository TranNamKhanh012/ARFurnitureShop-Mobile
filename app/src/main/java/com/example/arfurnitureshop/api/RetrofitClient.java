package com.example.arfurnitureshop.api;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // 1. THAY LINK THẬT CỦA BẠN VÀO ĐÂY (Link SmarterASP)
    //private static final String BASE_URL = "http://trannamkhanh-001-site1.jtempurl.com/";

    private static final String BASE_URL = "http://192.168.1.184:5103/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 2. LẤY THÔNG TIN TỪ PHẦN "PASSWORD PROTECTION" TRÊN SMARTERASP
            String userCam = "11300735";
            String passCam = "60-dayfreetrial";
            String authToken = Credentials.basic(userCam, passCam);

            // 3. CẤU HÌNH INTERCEPTOR ĐỂ VƯỢT RÀO
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", authToken)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // Gắn client đã có chìa khóa vào
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}