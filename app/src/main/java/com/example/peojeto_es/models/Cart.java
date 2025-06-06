package com.example.peojeto_es.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static Cart instance;
    private List<CartItem> items;
    private int currentStoreId = -1; // Nova variável para controlar loja atual

    private Cart() {
        items = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void addItem(Product product, int quantity, int storeId) {
        // Verifica se é primeira adição ou mesma loja
        if (currentStoreId == -1 || currentStoreId == storeId) {
            currentStoreId = storeId;

            // Procura item existente
            for (CartItem item : items) {
                if (item.getProduct().getId() == product.getId()) {
                    item.setQuantity(item.getQuantity() + quantity);
                    return;
                }
            }
            // Adiciona novo item
            items.add(new CartItem(product, quantity, storeId));
        } else {
            throw new IllegalStateException("Não é possível adicionar produtos de lojas diferentes");
        }
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        if (items.isEmpty()) {
            currentStoreId = -1; // Reset store ID quando carrinho fica vazio
        }
    }

    public void clear() {
        items.clear();
        currentStoreId = -1;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public int getCurrentStoreId() {
        return currentStoreId;
    }
}