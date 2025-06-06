// CartItemAdapter.java
package com.example.peojeto_es.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peojeto_es.models.CartItem;
import com.example.peojeto_es.R;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    private List<CartItem> items;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onRemoveClick(CartItem item);
    }

    public CartItemAdapter(List<CartItem> items, OnCartItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.nameText.setText(item.getProduct().getName());
        holder.priceText.setText(String.format("R$ %.2f", item.getProduct().getPrice()));
        holder.quantityText.setText(String.format("Quantidade: %d", item.getQuantity()));
        holder.subtotalText.setText(String.format("Subtotal: R$ %.2f",
                item.getProduct().getPrice() * item.getQuantity()));

        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, priceText, quantityText, subtotalText;
        ImageButton removeButton;

        CartItemViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.cartItemName);
            priceText = itemView.findViewById(R.id.cartItemPrice);
            quantityText = itemView.findViewById(R.id.cartItemQuantity);
            subtotalText = itemView.findViewById(R.id.cartItemSubtotal);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}