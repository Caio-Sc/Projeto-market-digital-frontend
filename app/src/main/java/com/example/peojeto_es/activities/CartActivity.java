// CartActivity.java
package com.example.peojeto_es.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peojeto_es.models.Cart;
import com.example.peojeto_es.models.CartItem;
import com.example.peojeto_es.adapters.CartItemAdapter;
import com.example.peojeto_es.R;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.OnCartItemListener {
    private RecyclerView cartRecyclerView;
    private CartItemAdapter cartAdapter;
    private TextView totalPriceText;
    private Button checkoutButton;
    private Cart cart;
    private String serverUrl = "http://10.0.2.2:3000/negocio/compra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart = Cart.getInstance();

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        checkoutButton = findViewById(R.id.checkoutButton);

        setupRecyclerView();
        updateTotalPrice();

        checkoutButton.setOnClickListener(v -> {
            if (!cart.getItems().isEmpty()) {
                processCheckout();
            } else {
                Toast.makeText(this, "Carrinho vazio!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartItemAdapter(cart.getItems(), this);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cart.getItems()) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        totalPriceText.setText(String.format("Total: R$ %.2f", total));
    }

    @Override
    public void onRemoveClick(CartItem item) {
        cart.removeItem(item);
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    private void processCheckout() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + LoginActivity.getToken(this));
                connection.setDoOutput(true);

                // Preparar dados da compra
                JSONObject purchaseData = new JSONObject();
                JSONArray produtos = new JSONArray();
                double totalCompra = 0;

                for (CartItem item : cart.getItems()) {
                    JSONObject produto = new JSONObject();
                    double precoUnitario = item.getProduct().getPrice();
                    double subtotal = precoUnitario * item.getQuantity();

                    produto.put("id", item.getProduct().getId());
                    produto.put("quantidade", item.getQuantity());
                    produto.put("preco_unitario", precoUnitario);
                    produto.put("subtotal", subtotal);

                    produtos.put(produto);
                    totalCompra += subtotal;
                }

                purchaseData.put("produtos", produtos);
                purchaseData.put("loja", new JSONObject().put("id", cart.getCurrentStoreId()));
                purchaseData.put("valor_total", totalCompra);

                // Enviar dados
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = purchaseData.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    handler.post(() -> {
                        Toast.makeText(this, "Compra realizada com sucesso!", Toast.LENGTH_LONG).show();
                        cart.clear();
                        finish();
                    });
                } else {
                    handler.post(() -> {
                        Toast.makeText(this, "Erro ao processar compra", Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}