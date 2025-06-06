// HomeActivity.java
package com.example.peojeto_es.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peojeto_es.R;
import com.example.peojeto_es.models.Store;
import com.example.peojeto_es.adapters.StoreAdapter;

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

public class HomeActivity extends AppCompatActivity implements StoreAdapter.OnStoreClickListener {
    private RecyclerView storesRecyclerView;
    private StoreAdapter storeAdapter;
    private String serverUrl = "http://10.0.2.2:3000/negocio";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton userButton = findViewById(R.id.userButton);
        ImageButton cartButton = findViewById(R.id.cartButton);
        storesRecyclerView = findViewById(R.id.storesRecyclerView);

        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UserSettingsActivity.class);
            startActivity(intent);
        });

        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });

        setupRecyclerView();
        fetchStores();
    }

    @Override
    public void onStoreClick(Store store) {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("store_id", store.getId());
        intent.putExtra("store_name", store.getName());
        startActivity(intent);
    }

    private void setupRecyclerView() {
        storeAdapter = new StoreAdapter(new ArrayList<>(), this);
        storesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        storesRecyclerView.setAdapter(storeAdapter);
    }

    private void fetchStores() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray storesArray = jsonResponse.getJSONArray("lojas");
                List<Store> stores = new ArrayList<>();

                for (int i = 0; i < storesArray.length(); i++) {
                    JSONObject storeObject = storesArray.getJSONObject(i);
                    Store store = new Store(
                            storeObject.getInt("id"),
                            storeObject.getString("nome"),
                            storeObject.getString("endereco"),
                            storeObject.getString("info")
                    );
                    stores.add(store);
                }

                handler.post(() -> storeAdapter.updateStores(stores));

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(HomeActivity.this,
                        "Erro ao carregar lojas: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        });
    }
}