// ProductListActivity.java
package com.example.peojeto_es.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peojeto_es.models.Product;
import com.example.peojeto_es.R;
import com.example.peojeto_es.models.Store;
import com.example.peojeto_es.adapters.StoreProductAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView productsRecyclerView;
    private StoreProductAdapter productAdapter; // Modificar esta linha
    private String serverUrl = "http://10.0.2.2:3000/negocio/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        int storeId = getIntent().getIntExtra("store_id", -1);
        String storeName = getIntent().getStringExtra("store_name");

        setTitle(storeName != null ? storeName : "Produtos");

        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        setupRecyclerView();

        if (storeId != -1) {
            fetchProducts(storeId);
        }
    }

    private void setupRecyclerView() {
        Store currentStore = new Store(
                getIntent().getIntExtra("store_id", -1),
                getIntent().getStringExtra("store_name"),
                "", // endereço
                ""  // info
        );
        productAdapter = new StoreProductAdapter(new ArrayList<>(), currentStore);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void fetchProducts(int storeId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(serverUrl + storeId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject productsArray = new JSONObject(response.toString());
                JSONArray produtosArray = productsArray.getJSONArray("produtos");
                List<Product> products = new ArrayList<>();

                for (int i = 0; i < produtosArray.length(); i++) {
                    JSONObject productObject = produtosArray.getJSONObject(i);
                    Product product = new Product(
                            productObject.getInt("id"),
                            productObject.getString("produto"),
                            productObject.getDouble("preco")
                    );
                    products.add(product);
                }

                handler.post(() -> productAdapter.updateProducts(products));

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(ProductListActivity.this,
                        "Erro ao carregar produtos: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        });
    }
}