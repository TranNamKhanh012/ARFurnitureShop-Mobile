package com.example.arfurnitureshop.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    private int id;
    private String name;

    @SerializedName("imageUrl") // Map đúng tên key từ JSON trả về
    private String imageUrl;

    public Category(int id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }

    // ĐÂY LÀ HÀM QUAN TRỌNG ĐỂ HẾT LỖI ĐỎ:
    public String getImageUrl() {
        return imageUrl;
    }
}