package com.example.arfurnitureshop.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    private int id;
    private String name;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("modelUrl")
    private String modelUrl;

    @SerializedName("price")
    private double price;

    // --- BẮT ĐẦU PHẦN THÊM MỚI ---

    // 1. Hàm khởi tạo không tham số (Bắt buộc cho Retrofit/Gson)
    public Product() {
    }

    // 2. Hàm khởi tạo có tham số (Dùng để tạo object khi lấy từ SQLite)
    public Product(int id, String name, String imageUrl, String modelUrl, double price) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.modelUrl = modelUrl;
        this.price = price;
    }

    // --- KẾT THÚC PHẦN THÊM MỚI ---

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getModelUrl() { return modelUrl; }
    public double getPrice() { return price; }
}