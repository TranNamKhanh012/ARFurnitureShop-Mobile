package com.example.arfurnitureshop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.arfurnitureshop.R;
import com.example.arfurnitureshop.models.Order;
import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        DecimalFormat df = new DecimalFormat("#,###");

        holder.tvOrderId.setText("Mã ĐH: #" + order.getId());

        // Cắt bớt phần giờ phút giây, chỉ lấy phần ngày (YYYY-MM-DD)
        String date = order.getOrderDate();
        if (date != null && date.contains("T")) {
            date = date.substring(0, date.indexOf("T"));
        }
        holder.tvOrderDate.setText("Ngày đặt: " + date);
        holder.tvOrderTotal.setText("Tổng tiền: ₫ " + df.format(order.getTotalAmount()));
        holder.tvOrderStatus.setText(order.getOrderStatus());

        // Đổi màu chữ theo trạng thái
        if ("Pending".equalsIgnoreCase(order.getOrderStatus())) {
            holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#FF9800")); // Cam
            holder.tvOrderStatus.setText("Chờ xác nhận");
        } else if ("Completed".equalsIgnoreCase(order.getOrderStatus())) {
            holder.tvOrderStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Xanh lá
            holder.tvOrderStatus.setText("Thành công");
        } else {
            holder.tvOrderStatus.setTextColor(android.graphics.Color.RED);
        }
        // Sự kiện click vào toàn bộ 1 dòng đơn hàng
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), com.example.arfurnitureshop.activities.OrderDetailActivity.class);
            // Gửi ID đơn hàng sang trang Chi tiết
            intent.putExtra("ORDER_ID", order.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal, tvOrderStatus;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}