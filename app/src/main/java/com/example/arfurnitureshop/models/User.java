package com.example.arfurnitureshop.models;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;

    public User() {}

    // Khởi tạo dùng cho Đăng ký
    public User(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = "User"; // Đăng ký thì vẫn gán quyền User bình thường
    }

    // Xóa cái hàm 2 tham số chứa dữ liệu giả đi nhé!

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}