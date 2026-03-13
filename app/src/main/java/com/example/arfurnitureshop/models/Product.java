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

    // ==========================================
    // KHAI BÁO THÊM 2 BIẾN MỚI
    // ==========================================
    @SerializedName("discount")
    private int discount;

    @SerializedName("rating")
    private double rating;

    // 1. Hàm khởi tạo không tham số (Bắt buộc cho Retrofit/Gson)
    public Product() {
    }

    // 2. Hàm khởi tạo có tham số (Đã cập nhật thêm discount và rating)
    public Product(int id, String name, String imageUrl, String modelUrl, double price, int discount, double rating) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.modelUrl = modelUrl;
        this.price = price;
        this.discount = discount;
        this.rating = rating;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getModelUrl() { return modelUrl; }
    public double getPrice() { return price; }

    // GETTER CHO BIẾN MỚI
    public int getDiscount() { return discount; }
    public double getRating() { return rating; }

    // ==========================================
    // HÀM TỰ ĐỘNG TÍNH GIÁ ĐÃ GIẢM
    // ==========================================
    public double getFinalPrice() {
        if (discount <= 0) {
            return price; // Không giảm giá thì giữ nguyên
        }
        return price - (price * discount / 100.0);
    }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }
    public void setPrice(double price) { this.price = price; }

    // SETTER CHO BIẾN MỚI
    public void setDiscount(int discount) { this.discount = discount; }
    public void setRating(double rating) { this.rating = rating; }
}