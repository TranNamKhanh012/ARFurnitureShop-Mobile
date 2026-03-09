package com.example.arfurnitureshop.models; // Sửa lại package cho đúng với project của bạn

import java.util.ArrayList;
import java.util.List;

public class WishlistManager {
    // Danh sách static này sẽ tồn tại suốt vòng đời của App
    public static List<Product> wishlistProducts = new ArrayList<>();

    // Thêm sản phẩm vào Wishlist (Chỉ thêm nếu chưa có)
    public static void addToWishlist(Product product) {
        boolean exists = false;
        for (Product p : wishlistProducts) {
            if (p.getId() == product.getId()) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            wishlistProducts.add(product);
        }
    }

    // Xóa sản phẩm khỏi Wishlist
    public static void removeFromWishlist(Product product) {
        for (int i = 0; i < wishlistProducts.size(); i++) {
            if (wishlistProducts.get(i).getId() == product.getId()) {
                wishlistProducts.remove(i);
                break;
            }
        }
    }

    // Kiểm tra xem sản phẩm đã có trong Wishlist chưa
    public static boolean isFavorite(int productId) {
        for (Product p : wishlistProducts) {
            if (p.getId() == productId) {
                return true;
            }
        }
        return false;
    }
}