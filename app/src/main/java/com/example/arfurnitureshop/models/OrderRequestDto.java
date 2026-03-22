package com.example.arfurnitureshop.models;

import java.util.List;

// File này dùng để đóng gói dữ liệu gửi lên API Orders/create
public class OrderRequestDto {
    public int userId;
    public double totalAmount;
    public String paymentMethod;
    public String shippingAddress;
    public String phoneNumber;
    public String receiverName;
    public List<OrderItemDto> items; // Danh sách các mặt hàng

    // Gói con để chứa thông tin từng món đồ
    public static class OrderItemDto {
        public int productId;
        public int quantity;
        public double unitPrice;

        // Constructor nhanh
        public OrderItemDto(int productId, int quantity, double unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }
}