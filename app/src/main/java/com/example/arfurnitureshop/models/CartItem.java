package com.example.arfurnitureshop.models;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Tính tổng tiền của món hàng này (Giá x Số lượng)
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}