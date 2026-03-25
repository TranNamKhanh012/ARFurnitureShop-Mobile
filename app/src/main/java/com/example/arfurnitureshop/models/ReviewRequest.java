package com.example.arfurnitureshop.models;

public class ReviewRequest {
    private int productId;
    private int userId;
    private String fullName;
    private int rating;
    private String comment;

    public ReviewRequest(int productId, int userId, String fullName, int rating, String comment) {
        this.productId = productId;
        this.userId = userId;
        this.fullName = fullName;
        this.rating = rating;
        this.comment = comment;
    }
}