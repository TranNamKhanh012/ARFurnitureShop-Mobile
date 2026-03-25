package com.example.arfurnitureshop.models;

public class Order {
    private int id;
    private String orderDate;
    private double totalAmount;
    private String orderStatus;
    private String paymentMethod;
    private String paymentStatus;

    public int getId() { return id; }
    public String getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getOrderStatus() { return orderStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
}