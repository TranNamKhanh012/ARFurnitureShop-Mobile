package com.example.arfurnitureshop.models;
import java.util.List;

public class OrderDetailResponse {
    private int id;
    private String receiverName;
    private String phoneNumber;
    private String shippingAddress;
    private double totalAmount;
    private String orderDate;
    private String orderStatus;
    private String paymentMethod;
    private String paymentStatus;
    private List<OrderItem> items;

    public int getId() { return id; }
    public String getReceiverName() { return receiverName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getShippingAddress() { return shippingAddress; }
    public double getTotalAmount() { return totalAmount; }
    public String getOrderDate() { return orderDate; }
    public String getOrderStatus() { return orderStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public List<OrderItem> getItems() { return items; }

    public static class OrderItem {
        private int productId;
        private String productName;
        private String productImage;
        private int quantity;
        private double unitPrice;
        private String selectedSize;

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getProductImage() { return productImage; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public String getSelectedSize() { return selectedSize; }
    }
}