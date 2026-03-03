package com.example.arfurnitureshop.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    private int id;
    private String name;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Thêm trường modelUrl để sau này chứa link file 3D (.glb) cho chức năng AR
    @SerializedName("modelUrl")
    private String modelUrl;

    @SerializedName("price")
    private double price;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getModelUrl() { return modelUrl; }
    public double getPrice() { return price; }
}