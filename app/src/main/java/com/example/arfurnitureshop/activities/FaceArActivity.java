package com.example.arfurnitureshop.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arfurnitureshop.R;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.TrackingState;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFrontFacingFragment;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.math.Quaternion;

import java.util.HashMap;

public class FaceArActivity extends AppCompatActivity {

    private ArFrontFacingFragment arFragment;
    private ModelRenderable glassesRenderable;
    private final HashMap<AugmentedFace, Node> faceNodeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_ar);

        arFragment = (ArFrontFacingFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        // 1. NHẬN LINK MÔ HÌNH 3D TỪ TRANG CHI TIẾT
        String modelUrl = getIntent().getStringExtra("PRODUCT_MODEL");

        // 2. BẢO VỆ KÉP: Nếu do lỗi mạng mà không nhận được Link thì tự động đóng Camera
        if (modelUrl == null || modelUrl.trim().isEmpty()) {
            Toast.makeText(this, "Lỗi dữ liệu mô hình 3D!", Toast.LENGTH_SHORT).show();
            finish(); // Lệnh đóng trang AR lại ngay lập tức
            return;   // Dừng không chạy các đoạn code bên dưới nữa
        }

        // 3. TẢI MÔ HÌNH 3D TỪ INTERNET
        ModelRenderable.builder()
                .setSource(this, android.net.Uri.parse(modelUrl))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    glassesRenderable = renderable;
                    glassesRenderable.setShadowCaster(false);
                    glassesRenderable.setShadowReceiver(false);
                    Toast.makeText(this, "Tải mô hình 3D thành công!", Toast.LENGTH_SHORT).show();
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Lỗi mạng: Không thể tải kính 3D", Toast.LENGTH_SHORT).show();
                    return null;
                });



        // 4. XỬ LÝ KHUÔN MẶT LIÊN TỤC (THỜI GIAN THỰC)
        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if (glassesRenderable == null) return;

            for (AugmentedFace face : arFragment.getArSceneView().getSession().getAllTrackables(AugmentedFace.class)) {
                if (face.getTrackingState() == TrackingState.TRACKING) {
                    Node faceCenterNode;

                    // Nếu phát hiện khuôn mặt mới
                    if (!faceNodeMap.containsKey(face)) {
                        faceCenterNode = new Node();
                        faceCenterNode.setParent(arFragment.getArSceneView().getScene());

                        Node glassesNode = new Node();
                        glassesNode.setParent(faceCenterNode);
                        glassesNode.setRenderable(glassesRenderable);

                        // Căn chỉnh tỷ lệ và vị trí của kính
                        glassesNode.setLocalScale(new Vector3(13.5f, 13.5f, 13.5f));
                        glassesNode.setLocalPosition(new Vector3(-0.185f, 0.0f, 0.0f));

                        faceNodeMap.put(face, faceCenterNode);
                    } else {
                        // Khuôn mặt đã có kính, lấy ra để cập nhật
                        faceCenterNode = faceNodeMap.get(face);
                    }

                    // Ép kính di chuyển và xoay theo từng nhịp lắc đầu của người dùng
                    Pose centerPose = face.getCenterPose();
                    faceCenterNode.setWorldPosition(new Vector3(centerPose.tx(), centerPose.ty(), centerPose.tz()));
                    faceCenterNode.setWorldRotation(new Quaternion(centerPose.qx(), centerPose.qy(), centerPose.qz(), centerPose.qw()));
                }
            }
        });
    }
}