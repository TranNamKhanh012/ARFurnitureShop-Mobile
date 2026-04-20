package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.arfurnitureshop.R;

// Import thư viện AI của Google
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

public class PhotoTryOnActivity extends AppCompatActivity {

    private ImageView ivUserFace;
    private TextView tvInstruction;
    private String productImageUrl;
    private Bitmap glassesBitmap; // Chứa ảnh kính tải từ server
    private Bitmap userFaceBitmap; // Chứa ảnh mặt khách hàng

    // Cảm biến chọn ảnh từ thư viện
    private final ActivityResultLauncher<Intent> photoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        Uri selectedImageUri = result.getData().getData();
                        // Chuyển ảnh khách hàng thành Bitmap để AI có thể đọc
                        userFaceBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                        // Copy ra 1 bản để có thể "vẽ" kính đè lên
                        userFaceBitmap = userFaceBitmap.copy(Bitmap.Config.ARGB_8888, true);

                        ivUserFace.setImageBitmap(userFaceBitmap);
                        tvInstruction.setText("Đang dùng AI nhận diện khuôn mặt...");
                        tvInstruction.setVisibility(View.VISIBLE);

                        // Bắt đầu gọi AI phân tích
                        runAIFaceDetection();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_try_on);

        View headerView = findViewById(R.id.headerTryOn);
        TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
        if(tvTitle != null) tvTitle.setText("Thử Kính AI Auto");
        headerView.findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        ivUserFace = findViewById(R.id.ivUserFace);
        tvInstruction = findViewById(R.id.tvInstruction);
        Button btnSelectPhoto = findViewById(R.id.btnSelectPhoto);

        productImageUrl = getIntent().getStringExtra("PRODUCT_IMAGE");

        // Khi vừa vào trang, lập tức tải ảnh kính nền trong suốt từ SmarterASP về cất sẵn vào biến
        downloadGlassesImageFromAPI();

        btnSelectPhoto.setOnClickListener(v -> {
            if (glassesBitmap == null) {
                Toast.makeText(this, "Đang tải dữ liệu kính, vui lòng đợi giây lát!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            photoPickerLauncher.launch(intent);
        });
    }

    // ===============================================
    // 1. TẢI ẢNH KÍNH TỪ SERVER (BẰNG GLIDE)
    // ===============================================
    private void downloadGlassesImageFromAPI() {
        if (productImageUrl != null && !productImageUrl.isEmpty()) {
            String fullImageUrl = "http://trannamkhanh-001-site1.jtempurl.com/images/" + productImageUrl;
            String credential = okhttp3.Credentials.basic("11300735", "60-dayfreetrial");

            com.bumptech.glide.load.model.GlideUrl glideUrlWithAuth = new com.bumptech.glide.load.model.GlideUrl(fullImageUrl,
                    new com.bumptech.glide.load.model.LazyHeaders.Builder().addHeader("Authorization", credential).build());

            Glide.with(this).asBitmap().load(glideUrlWithAuth).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    glassesBitmap = resource; // Cất ảnh kính vào biến
                    tvInstruction.setText("Hãy chọn 1 bức ảnh rõ mặt của bạn");
                }
                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {}
            });
        }
    }

    // ===============================================
    // 2. DÙNG GOOGLE AI ĐỂ QUÉT TÌM CON MẮT
    // ===============================================
    private void runAIFaceDetection() {
        // Cấu hình AI: Yêu cầu tìm các điểm mốc (Landmark - như mắt, mũi, miệng)
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .build();

        InputImage image = InputImage.fromBitmap(userFaceBitmap, 0);
        FaceDetector detector = FaceDetection.getClient(options);

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (faces.isEmpty()) {
                        tvInstruction.setText("Không tìm thấy khuôn mặt nào trong ảnh!");
                        return;
                    }

                    // Lấy khuôn mặt đầu tiên tìm được
                    Face face = faces.get(0);
                    FaceLandmark leftEye = face.getLandmark(FaceLandmark.LEFT_EYE);
                    FaceLandmark rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE);

                    if (leftEye != null && rightEye != null) {
                        // Gửi tọa độ 2 mắt sang hàm vẽ kính
                        overlayGlasses(leftEye.getPosition(), rightEye.getPosition());
                        tvInstruction.setVisibility(View.GONE);
                    } else {
                        tvInstruction.setText("Ảnh bị che khuất mắt, không thể ghép kính!");
                    }
                })
                .addOnFailureListener(e -> tvInstruction.setText("Lỗi AI: " + e.getMessage()));
    }

    // ===============================================
    // 3. TOÁN HỌC: GHÉP KÍNH VÀO TỌA ĐỘ MẮT
    // ===============================================
    private void overlayGlasses(PointF leftEye, PointF rightEye) {
        Canvas canvas = new Canvas(userFaceBitmap);

        // a. Tính khoảng cách giữa 2 mắt
        float dx = rightEye.x - leftEye.x;
        float dy = rightEye.y - leftEye.y;
        float distanceBetweenEyes = (float) Math.sqrt(dx * dx + dy * dy);

        // b. Tính góc nghiêng của đầu (để kính xoay theo)
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));

        // c. Tính tâm điểm giữa 2 mắt (Đặt sống kính vào đây)
        float centerX = (leftEye.x + rightEye.x) / 2;
        float centerY = (leftEye.y + rightEye.y) / 2;

        // d. Tính tỷ lệ phóng to kính (Thường chiều ngang kính gấp 2.2 lần khoảng cách 2 mắt)
        float glassScale = (distanceBetweenEyes * 2.2f) / glassesBitmap.getWidth();

        // e. Tiến hành vẽ ảnh kính đè lên
        Matrix matrix = new Matrix();
        matrix.postTranslate(-glassesBitmap.getWidth() / 2f, -glassesBitmap.getHeight() / 2f); // Đưa tâm kính về gốc
        matrix.postScale(glassScale, glassScale); // Phóng to/thu nhỏ
        matrix.postRotate(angle); // Xoay nghiêng
        matrix.postTranslate(centerX, centerY); // Đặt vào giữa 2 mắt

        canvas.drawBitmap(glassesBitmap, matrix, null);

        // Cập nhật ảnh lên màn hình
        ivUserFace.setImageBitmap(userFaceBitmap);
        Toast.makeText(this, "Ghép kính thành công!", Toast.LENGTH_SHORT).show();
    }
}