// ProductAdapter.java
package com.example.peojeto_es.adapters;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peojeto_es.models.Product;
import com.example.peojeto_es.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    protected List<Product> products;
    private final OnProductListener onProductListener;
    private boolean isAdminView;

    public interface OnProductListener {
        void onDeleteClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductListener onProductListener, boolean isAdminView) {
        this.products = products;
        this.onProductListener = onProductListener;
        this.isAdminView = isAdminView;
    }

    protected List<Product> getProducts() {
        return products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(isAdminView ? R.layout.item_product_delete : R.layout.item_product_store, parent, false);
        return new ProductViewHolder(view, isAdminView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.nameText.setText(product.getName());
        holder.priceText.setText(String.format("R$ %.2f", product.getPrice()));

        if (isAdminView && holder.deleteButton != null) {
            holder.deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Confirmar exclusão")
                        .setMessage("Deseja realmente excluir este produto?")
                        .setPositiveButton("Sim", (dialog, which) -> onProductListener.onDeleteClick(product))
                        .setNegativeButton("Não", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        products = newProducts;
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, priceText;
        ImageButton deleteButton;

        ProductViewHolder(View itemView, boolean isAdminView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.productName);
            priceText = itemView.findViewById(R.id.productPrice);
            if (isAdminView) {
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
}