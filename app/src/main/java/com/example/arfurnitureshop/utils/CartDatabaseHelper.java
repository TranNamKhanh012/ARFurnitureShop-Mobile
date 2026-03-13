package com.example.arfurnitureshop.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.arfurnitureshop.models.CartItem;
import com.example.arfurnitureshop.models.Product;
import java.util.ArrayList;
import java.util.List;

public class CartDatabaseHelper extends SQLiteOpenHelper {
    public CartDatabaseHelper(Context context) {
        super(context, "CartDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng giỏ hàng
        db.execSQL("CREATE TABLE cart(id INTEGER PRIMARY KEY, name TEXT, price REAL, image TEXT, quantity INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

    // Hàm Thêm hoặc Cập nhật số lượng
    public void addOrUpdateItem(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("id", item.getProduct().getId());
        v.put("name", item.getProduct().getName());
        v.put("price", item.getProduct().getPrice());
        v.put("image", item.getProduct().getImageUrl());
        v.put("quantity", item.getQuantity());

        // CONFLICT_REPLACE: Nếu trùng ID sản phẩm thì ghi đè số lượng mới
        db.insertWithOnConflict("cart", null, v, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Hàm Lấy toàn bộ giỏ hàng
    public List<CartItem> getAllItems() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cart", null);

        if (c.moveToFirst()) {
            do {
                // Khởi tạo Product từ SQLite (cần có Constructor 5 tham số trong Product.java)
                Product p = new Product(c.getInt(0), c.getString(1), c.getString(3), "", c.getDouble(2), 0, 5.0);
                list.add(new CartItem(p, c.getInt(4)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    // Hàm Xóa sản phẩm khỏi giỏ
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
    // Hàm dọn sạch toàn bộ giỏ hàng trong SQLite
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM cart"); // Xóa toàn bộ dữ liệu trong bảng cart
        db.close();
    }
}