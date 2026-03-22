package com.example.arfurnitureshop.models;

public class UserAddress implements java.io.Serializable{
    private int id;
    private int userId;
    private String receiverName;
    private String phoneNumber;
    private String fullAddress;
    private boolean isDefault;

    // 1. Constructor dùng khi TẠO MỚI (Không cần truyền ID)
    public UserAddress(int userId, String receiverName, String phoneNumber, String fullAddress, boolean isDefault) {
        this.userId = userId;
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.fullAddress = fullAddress;
        this.isDefault = isDefault;
    }

    // 2. ĐÃ THÊM: Constructor dùng khi SỬA ĐỊA CHỈ (Cần truyền chính xác ID cũ)
    public UserAddress(int id, int userId, String receiverName, String phoneNumber, String fullAddress, boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.fullAddress = fullAddress;
        this.isDefault = isDefault;
    }

    // Các hàm Getters...
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getReceiverName() { return receiverName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getFullAddress() { return fullAddress; }
    public boolean isDefault() { return isDefault; }
}