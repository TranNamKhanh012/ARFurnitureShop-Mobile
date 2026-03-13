package com.example.arfurnitureshop.models;

import java.util.ArrayList;
import java.util.List;

public class WishlistManager {
    public static List<Product> wishlistProducts = new ArrayList<>();

    // 1. Kiểm tra xem sản phẩm đã thả tim chưa
    public static boolean isFavorite(int productId) {
        for (Product p : wishlistProducts) {
            if (p.getId() == productId) return true;
        }
        return false;
    }

    // 2. Thêm vào danh sách thả tim
    public static void add(Product product) {
        if (!isFavorite(product.getId())) {
            wishlistProducts.add(product);
        }
    }

    // 3. XÓA khỏi danh sách thả tim (Dựa vào ID)
    public static void remove(int productId) {
        for (int i = 0; i < wishlistProducts.size(); i++) {
            if (wishlistProducts.get(i).getId() == productId) {
                wishlistProducts.remove(i);
                break;
            }
        }
    }

    // 4. Dọn sạch khi Đăng xuất
    public static void clear() {
        if (wishlistProducts != null) {
            wishlistProducts.clear();
        }
    }
}