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
        super(context, "CartDB", null, 3); // Lên version 3 để lưu Discount
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE cart(id INTEGER, name TEXT, price REAL, image TEXT, quantity INTEGER, size TEXT, discount INTEGER, PRIMARY KEY(id, size))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

    public void addOrUpdateItem(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("id", item.getProduct().getId());
        v.put("name", item.getProduct().getName());
        v.put("price", item.getProduct().getPrice()); // LƯU GIÁ GỐC
        v.put("image", item.getProduct().getImageUrl());
        v.put("quantity", item.getQuantity());
        v.put("size", item.getSelectedSize() != null ? item.getSelectedSize() : "");
        v.put("discount", item.getProduct().getDiscount()); // LƯU % GIẢM GIÁ

        db.insertWithOnConflict("cart", null, v, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<CartItem> getAllItems() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM cart", null);

        if (c.moveToFirst()) {
            do {
                Product p = new Product();
                p.setId(c.getInt(0));
                p.setName(c.getString(1));
                p.setPrice(c.getDouble(2)); // Lấy giá gốc
                p.setImageUrl(c.getString(3));
                p.setRating(5.0);
                p.setDiscount(c.getInt(6)); // Lấy % giảm giá (Cột 6)

                String size = c.getString(5);
                list.add(new CartItem(p, c.getInt(4), size));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public void deleteItem(int id, String size) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(size == null) size = "";
        db.delete("cart", "id=? AND size=?", new String[]{String.valueOf(id), size});
        db.close();
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM cart");
        db.close();
    }
}