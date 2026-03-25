package com.example.arfurnitureshop.models;

public class ReviewResponse {
    private int id;
    private String fullName;
    private int rating;
    private String comment;
    private String createdAt;

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}