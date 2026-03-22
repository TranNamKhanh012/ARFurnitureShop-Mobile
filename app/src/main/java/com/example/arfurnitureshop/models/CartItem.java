package com.example.arfurnitureshop.models;

public class CartItem {
    private Product product;
    private int quantity;
    private String selectedSize; // [MỚI] Biến lưu Size khách chọn

    public CartItem(Product product, int quantity, String selectedSize) {
        this.product = product;
        this.quantity = quantity;
        this.selectedSize = selectedSize;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public double getTotalPrice() {
        double finalPrice = product.getPrice();
        if (product.getDiscount() > 0) {
            finalPrice = finalPrice - (finalPrice * product.getDiscount() / 100.0);
        }
        return finalPrice * quantity;
    }
}