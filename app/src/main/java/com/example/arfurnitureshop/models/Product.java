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

    @SerializedName("discount")
    private int discount;

    @SerializedName("rating")
    private double rating;

    @SerializedName("description")
    private String description;

    // ==========================================
    // 2 BIẾN MỚI CHO TÌM KIẾM & SẮP XẾP NÂNG CAO
    // ==========================================
    @SerializedName("reviewCount")
    private int reviewCount;

    @SerializedName("dateAdded")
    private String dateAdded;
    @SerializedName("sizes")
    private String sizes;

    public String getSizes() { return sizes; }
    public void setSizes(String sizes) { this.sizes = sizes; }

    // 1. Hàm khởi tạo không tham số
    public Product() {
    }

    // 2. Hàm khởi tạo có tham số
    public Product(int id, String name, String imageUrl, String modelUrl, double price, int discount, double rating, String description, int reviewCount, String dateAdded) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.modelUrl = modelUrl;
        this.price = price;
        this.discount = discount;
        this.rating = rating;
        this.description = description;
        this.reviewCount = reviewCount;
        this.dateAdded = dateAdded;
    }

    // ==========================================
    // HÀM TỰ ĐỘNG TÍNH GIÁ ĐÃ GIẢM
    // ==========================================
    public double getFinalPrice() {
        if (discount <= 0) {
            return price; // Không giảm giá thì giữ nguyên
        }
        return price - (price * discount / 100.0);
    }

    // ==========================================
    // GETTERS
    // ==========================================
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getModelUrl() { return modelUrl; }
    public double getPrice() { return price; }
    public int getDiscount() { return discount; }
    public double getRating() { return rating; }
    public String getDescription() { return description; }
    public int getReviewCount() { return reviewCount; }
    public String getDateAdded() { return dateAdded; }

    // ==========================================
    // SETTERS
    // ==========================================
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }
    public void setPrice(double price) { this.price = price; }
    public void setDiscount(int discount) { this.discount = discount; }
    public void setRating(double rating) { this.rating = rating; }
    public void setDescription(String description) { this.description = description; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }
}