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

    // THAY ĐỔI 1: Dùng Node bình thường thay vì AugmentedFaceNode để KHÔNG bị đen mặt
    private final HashMap<AugmentedFace, Node> faceNodeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_ar);

        arFragment = (ArFrontFacingFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        String githubModelUrl = "https://raw.githubusercontent.com/TranNamKhanh012/AR-Models/main/balenciaga_sunglasses.glb";

        ModelRenderable.builder()
                .setSource(this, android.net.Uri.parse(githubModelUrl))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    glassesRenderable = renderable;
                    glassesRenderable.setShadowCaster(false);
                    glassesRenderable.setShadowReceiver(false);
                    Toast.makeText(this, "Đã tải xong kính từ GitHub!", Toast.LENGTH_SHORT).show();
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Lỗi mạng: Không thể tải kính 3D", Toast.LENGTH_SHORT).show();
                    return null;
                });

        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {

            if (glassesRenderable == null) return;

            for (AugmentedFace face : arFragment.getArSceneView().getSession().getAllTrackables(AugmentedFace.class)) {

                if (face.getTrackingState() == TrackingState.TRACKING) {

                    Node faceCenterNode;

                    // Nếu là khuôn mặt mới xuất hiện
                    if (!faceNodeMap.containsKey(face)) {

                        // 1. Tạo một điểm neo tàng hình ở giữa mặt
                        faceCenterNode = new Node();
                        faceCenterNode.setParent(arFragment.getArSceneView().getScene());

                        // 2. Gắn kính vào điểm neo đó
                        Node glassesNode = new Node();
                        glassesNode.setParent(faceCenterNode);
                        glassesNode.setRenderable(glassesRenderable);

                        // ===============================================
                        // KHÁNH CHỈNH KÍCH THƯỚC VÀ TỌA ĐỘ KÍNH Ở ĐÂY NHÉ
                        // ===============================================
                        glassesNode.setLocalScale(new Vector3(0.8f, 0.6f, 0.2f));

                        // Căn chỉnh 2: Đưa kính lên mắt (Y = 0.03f) và đẩy ra trước (Z = 0.06f)
                        glassesNode.setLocalPosition(new Vector3(0.0f, 0.02f, 0.06f));


                        faceNodeMap.put(face, faceCenterNode);
                    } else {
                        // Nếu mặt đã có kính rồi thì lấy ra để cập nhật vị trí
                        faceCenterNode = faceNodeMap.get(face);
                    }

                    // THAY ĐỔI 2: Liên tục bắt cái kính chạy theo đầu của bạn mỗi khi bạn lắc đầu
                    Pose centerPose = face.getCenterPose();
                    faceCenterNode.setWorldPosition(new Vector3(centerPose.tx(), centerPose.ty(), centerPose.tz()));
                    faceCenterNode.setWorldRotation(new Quaternion(centerPose.qx(), centerPose.qy(), centerPose.qz(), centerPose.qw()));
                }
            }
        });
    }
}