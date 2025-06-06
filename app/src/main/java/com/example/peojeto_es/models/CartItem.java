package com.example.peojeto_es.models;

public class CartItem {
    private Product product;
    private int quantity;
    private int storeId; // Novo campo

    public CartItem(Product product, int quantity, int storeId) {
        this.product = product;
        this.quantity = quantity;
        this.storeId = storeId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStoreId() {
        return storeId;
    }
}