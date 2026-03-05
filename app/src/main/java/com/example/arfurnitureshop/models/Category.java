package com.example.arfurnitureshop.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    private int id;
    private String name;

    @SerializedName("imageUrl")
    private String imageUrl;

    // 1. Constructor rỗng cực kỳ quan trọng cho Retrofit/GSON
    public Category() {
    }

    public Category(int id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // 2. PHẢI CÓ hàm getId() để truyền sang màn hình lọc sản phẩm
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // (Tùy chọn) Thêm các hàm set nếu bạn cần gán thủ công sau này
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}