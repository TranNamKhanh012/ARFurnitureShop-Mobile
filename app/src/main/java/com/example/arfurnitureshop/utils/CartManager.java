package com.example.arfurnitureshop.utils;

import android.content.Context;
import com.example.arfurnitureshop.models.CartItem;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private CartDatabaseHelper dbHelper;

    // Bắt buộc phải truyền Context để khởi tạo Database
    private CartManager(Context context) {
        dbHelper = new CartDatabaseHelper(context);
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    // --- CÁC HÀM TƯƠNG TÁC ĐƯỢC GỌI TỪ ADAPTER VÀ ACTIVITY ---

    public void add(CartItem item) {
        dbHelper.addOrUpdateItem(item);
    }

    public List<CartItem> getItems() {
        return dbHelper.getAllItems();
    }

    public void remove(int id) {
        dbHelper.deleteItem(id);
    }

    // Hàm tính tổng tiền cho màn hình CartActivity
    public double getTotal() {
        double total = 0;
        for (CartItem item : getItems()) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }
    public void clear() {
        // Gọi thẳng lệnh xóa DB thay vì xóa cái list tạm
        if (dbHelper != null) {
            dbHelper.clearCart();
        }

    }

}