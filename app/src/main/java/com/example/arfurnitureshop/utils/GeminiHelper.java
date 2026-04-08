package com.example.arfurnitureshop.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiHelper {

    // ĐIỀN API KEY CỦA BẠN VÀO GIỮA 2 DẤU NGOẶC KÉP BÊN DƯỚI
    private static final String API_KEY = "teo them";

    // Mình đã thêm .trim() để tự động xóa các dấu cách thừa nếu bạn lỡ copy nhầm
    // Dùng lại model gemini-1.5-flash tiêu chuẩn
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY.trim();

    private static final OkHttpClient client = new OkHttpClient();

    public interface ResponseCallback {
        void onSuccess(String responseText);
        void onError(String errorMessage);
    }

    public static void askAI(String userMessage, ResponseCallback callback) {
        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contentsArray = new JSONArray();
            JSONObject contentsObject = new JSONObject();
            JSONArray partsArray = new JSONArray();
            JSONObject partsObject = new JSONObject();

            String promptContext = "Bạn là trợ lý ảo AI thông minh của ứng dụng Vũ Trụ Phụ Kiện. Hãy trả lời ngắn gọn, thân thiện và hữu ích. Khách hàng hỏi: " + userMessage;

            partsObject.put("text", promptContext);
            partsArray.put(partsObject);
            contentsObject.put("parts", partsArray);
            contentsArray.put(contentsObject);
            jsonBody.put("contents", contentsArray);

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody.toString());

            // Mình đã bổ sung thêm addHeader để ép Google hiểu đây là file JSON
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Lỗi kết nối mạng: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // ==========================================
                    // ĐOẠN NÀY LÀ VŨ KHÍ BÍ MẬT ĐỂ TÌM LỖI
                    // ==========================================
                    if (!response.isSuccessful()) {
                        // Đọc thẳng nội dung thư rác mà Google trả về
                        String errorBody = response.body() != null ? response.body().string() : "Không có chi tiết lỗi";
                        Log.e("GEMINI_ERROR", "Mã lỗi: " + response.code() + " | Chi tiết: " + errorBody);

                        // Bắn thông báo dài ra màn hình để bạn đọc được
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("Lỗi " + response.code() + ": " + errorBody));
                        return;
                    }

                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray candidates = jsonResponse.getJSONArray("candidates");
                        JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                        JSONArray parts = content.getJSONArray("parts");

                        String aiAnswer = parts.getJSONObject(0).getString("text");

                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(aiAnswer));

                    } catch (Exception e) {
                        Log.e("GEMINI_PARSE_ERROR", "Lỗi đọc JSON: " + e.getMessage());
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("Lỗi đọc dữ liệu AI: " + e.getMessage()));
                    }
                }
            });

        } catch (Exception e) {
            callback.onError("Lỗi khởi tạo AI: " + e.getMessage());
        }
    }
}