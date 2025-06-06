package com.example.peojeto_es.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.util.Log;

import com.example.peojeto_es.models.Product;
import com.example.peojeto_es.adapters.ProductAdapter;
import com.example.peojeto_es.R;

public class ManageProductsActivity extends AppCompatActivity implements ProductAdapter.OnProductListener {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private String serverUrl = "http://10.0.2.2:3000/negocio";
    String TAG = "MeuApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        recyclerView = findViewById(R.id.productsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Modificar esta linha para incluir true como isAdminView
        adapter = new ProductAdapter(new ArrayList<>(), this, true);
        recyclerView.setAdapter(adapter);

        loadProducts();
    }

    private void loadProducts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {

                // Primeiro, precisamos pegar o ID da loja do vendedor
                URL vendedorUrl = new URL(serverUrl + "/vendedorLoja");
                HttpURLConnection vendedorConn = (HttpURLConnection) vendedorUrl.openConnection();
                vendedorConn.setRequestMethod("POST");
                vendedorConn.setRequestProperty("Authorization", "Bearer " + LoginActivity.getToken(this));

                JSONObject vendedorResponse = getJsonResponse(vendedorConn);
                Log.d(TAG, "test: " + vendedorResponse);
                int lojaId = vendedorResponse.getInt("loja");
                Log.d(TAG, "" + lojaId);
                // Agora podemos pegar os produtos da loja
                URL produtosUrl = new URL(serverUrl + "/" + lojaId);
                HttpURLConnection produtosConn = (HttpURLConnection) produtosUrl.openConnection();
                produtosConn.setRequestMethod("GET");
                //produtosConn.setRequestProperty("Authorization", "Bearer " + LoginActivity.getToken(this));

                JSONObject produtosResponse = getJsonResponse(produtosConn);
                Log.d(TAG, "" + produtosResponse);
                JSONArray produtosArray = produtosResponse.getJSONArray("produtos");
                Log.d(TAG, "" + produtosArray);
                List<Product> products = new ArrayList<>();

                for (int i = 0; i < produtosArray.length(); i++) {
                    JSONObject produto = produtosArray.getJSONObject(i);
                    products.add(new Product(
                            produto.getInt("id"),
                            produto.getString("produto"),
                            produto.getDouble("preco")
                    ));
                }

                handler.post(() -> adapter.updateProducts(products));

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(ManageProductsActivity.this,
                        "Erro ao carregar produtos: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        });
    }

    private JSONObject getJsonResponse(HttpURLConnection connection) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        return new JSONObject(response.toString());
    }

    @Override
    public void onDeleteClick(Product product) {
        deleteProduct(product.getId());
    }

    private void deleteProduct(int productId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(serverUrl + "/produto/" + productId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Authorization", "Bearer " + LoginActivity.getToken(this));

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    handler.post(() -> {
                        Toast.makeText(this, "Produto deletado com sucesso!", Toast.LENGTH_SHORT).show();
                        loadProducts(); // Recarrega a lista
                    });
                } else {
                    handler.post(() -> Toast.makeText(this,
                            "Erro ao deletar produto", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this,
                        "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}