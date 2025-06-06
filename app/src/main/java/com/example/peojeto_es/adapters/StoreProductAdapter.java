package com.example.peojeto_es.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.peojeto_es.models.Cart;
import com.example.peojeto_es.models.Product;
import com.example.peojeto_es.R;
import com.example.peojeto_es.models.Store;

import java.util.List;

public class StoreProductAdapter extends ProductAdapter {
    private Cart cart;
    private Store store; // Novo campo

    public StoreProductAdapter(List<Product> products, Store store) { // Atualizar construtor
        super(products, null, false);
        this.cart = Cart.getInstance();
        this.store = store;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_store, parent, false);
        return new StoreProductViewHolder(view, false); // false para isAdminView
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = getProducts().get(position);
        StoreProductViewHolder storeHolder = (StoreProductViewHolder) holder;
        
        storeHolder.nameText.setText(product.getName());
        storeHolder.priceText.setText(String.format("R$ %.2f", product.getPrice()));
        storeHolder.quantityText.setText("0");

        storeHolder.decreaseButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(storeHolder.quantityText.getText().toString());
            if (quantity > 0) {
                storeHolder.quantityText.setText(String.valueOf(quantity - 1));
            }
        });

        storeHolder.increaseButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(storeHolder.quantityText.getText().toString());
            storeHolder.quantityText.setText(String.valueOf(quantity + 1));
        });

        storeHolder.addToCartButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(storeHolder.quantityText.getText().toString());
            if (quantity > 0) {
                try {
                    cart.addItem(product, quantity, store.getId());
                    Toast.makeText(v.getContext(),
                            "Adicionado " + quantity + " " + product.getName() + " ao carrinho",
                            Toast.LENGTH_SHORT).show();
                    storeHolder.quantityText.setText("0");
                } catch (IllegalStateException e) {
                    Toast.makeText(v.getContext(),
                            "Não é possível adicionar produtos de lojas diferentes",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    static class StoreProductViewHolder extends ProductViewHolder {
        TextView quantityText;
        ImageButton decreaseButton;
        ImageButton increaseButton;
        Button addToCartButton;

        StoreProductViewHolder(View itemView, boolean isAdminView) {
            super(itemView, isAdminView);
            quantityText = itemView.findViewById(R.id.txtQuantity);
            decreaseButton = itemView.findViewById(R.id.btnDecrease);
            increaseButton = itemView.findViewById(R.id.btnIncrease);
            addToCartButton = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}