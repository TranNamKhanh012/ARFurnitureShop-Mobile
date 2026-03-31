package com.example.arfurnitureshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.utils.GeminiHelper;

import java.util.ArrayList;
import java.util.List;

public class ChatAiActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessageInput;
    private ImageView btnSend;

    private List<ChatMessage> chatList;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ai);

        // 1. Cài đặt Header
        View headerView = findViewById(R.id.headerChatAi);
        if (headerView != null) {
            TextView tvTitle = headerView.findViewById(R.id.tvHeaderTitle);
            if (tvTitle != null) tvTitle.setText("Trợ lý AI");

            ImageView btnBack = headerView.findViewById(R.id.btnBack);
            if (btnBack != null) btnBack.setOnClickListener(v -> finish());

            ImageView btnHome = headerView.findViewById(R.id.btnHome);
            if (btnHome != null) {
                btnHome.setOnClickListener(v -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        // 2. Ánh xạ View Chat
        rvChat = findViewById(R.id.rvChat);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSend = findViewById(R.id.btnSend);

        // 3. Cài đặt danh sách Chat
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Tự động cuộn xuống cuối cùng khi có tin nhắn mới
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatAdapter);

        // Thêm câu chào mừng của AI khi vừa mở màn hình
        addMessage("Chào bạn! Mình là trợ lý AI thông minh. Mình có thể giúp gì cho bạn hôm nay?", false);

        // 4. Xử lý khi bấm nút Gửi
        btnSend.setOnClickListener(v -> {
            String userText = etMessageInput.getText().toString().trim();
            if (userText.isEmpty()) return;

            // Xóa nội dung ô nhập liệu
            etMessageInput.setText("");

            // Hiển thị tin nhắn của người dùng lên màn hình
            addMessage(userText, true);

            // Tạo một tin nhắn ảo "..." để báo hiệu AI đang suy nghĩ
            chatList.add(new ChatMessage("...", false));
            int aiThinkingPosition = chatList.size() - 1;
            chatAdapter.notifyItemInserted(aiThinkingPosition);
            rvChat.smoothScrollToPosition(aiThinkingPosition);

            // GỌI API GEMINI (Sử dụng GeminiHelper bạn đã tạo)
            GeminiHelper.askAI(userText, new GeminiHelper.ResponseCallback() {
                @Override
                public void onSuccess(String responseText) {
                    // Xóa chữ "..." và cập nhật lại bằng câu trả lời thật của AI
                    chatList.get(aiThinkingPosition).setText(responseText);
                    chatAdapter.notifyItemChanged(aiThinkingPosition);
                    rvChat.smoothScrollToPosition(aiThinkingPosition);
                }

                @Override
                public void onError(String errorMessage) {
                    chatList.get(aiThinkingPosition).setText("Xin lỗi, mình đang gặp sự cố kết nối. Bạn thử lại nhé!");
                    chatAdapter.notifyItemChanged(aiThinkingPosition);
                    Toast.makeText(ChatAiActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void addMessage(String text, boolean isUser) {
        chatList.add(new ChatMessage(text, isUser));
        chatAdapter.notifyItemInserted(chatList.size() - 1);
        rvChat.smoothScrollToPosition(chatList.size() - 1);
    }

    // =========================================================
    // INNER CLASSES: GỘP CHUNG MODEL VÀ ADAPTER CHO GỌN NHẸ
    // =========================================================

    // 1. Model dữ liệu 1 dòng tin nhắn
    public static class ChatMessage {
        private String text;
        private final boolean isUser;

        public ChatMessage(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public boolean isUser() { return isUser; }
    }

    // 2. Adapter điều khiển giao diện bong bóng chat
    public static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private final List<ChatMessage> messages;

        public ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage message = messages.get(position);

            if (message.isUser()) {
                // Nếu là User: Hiện thẻ bên phải, ẩn thẻ bên trái
                holder.cardUser.setVisibility(View.VISIBLE);
                holder.cardAi.setVisibility(View.GONE);
                holder.tvUserText.setText(message.getText());
            } else {
                // Nếu là AI: Hiện thẻ bên trái, ẩn thẻ bên phải
                holder.cardAi.setVisibility(View.VISIBLE);
                holder.cardUser.setVisibility(View.GONE);
                holder.tvAiText.setText(message.getText());
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class ChatViewHolder extends RecyclerView.ViewHolder {
            CardView cardAi, cardUser;
            TextView tvAiText, tvUserText;

            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                cardAi = itemView.findViewById(R.id.cardAi);
                cardUser = itemView.findViewById(R.id.cardUser);
                tvAiText = itemView.findViewById(R.id.tvAiText);
                tvUserText = itemView.findViewById(R.id.tvUserText);
            }
        }
    }
}