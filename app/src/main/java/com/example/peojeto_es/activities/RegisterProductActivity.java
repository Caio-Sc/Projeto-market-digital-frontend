package com.example.peojeto_es.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.peojeto_es.R;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterProductActivity extends AppCompatActivity {
    private EditText productNameField, productPriceField;
    private String serverUrl = "http://10.0.2.2:3000/negocio/registroProduto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);

        productNameField = findViewById(R.id.productNameField);
        productPriceField = findViewById(R.id.productPriceField);
        Button registerButton = findViewById(R.id.registerProductButton);

        registerButton.setOnClickListener(v -> registerProduct());
    }

    private void registerProduct() {
        String nome = productNameField.getText().toString().trim();
        String precoStr = productPriceField.getText().toString().trim();

        if (nome.isEmpty() || precoStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double preco;
        try {
            preco = Double.parseDouble(precoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Preço inválido", Toast.LENGTH_SHORT).show();
            return;
        }

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

                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("produto", nome);
                jsonRequest.put("preco", preco);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == 200 || responseCode == 201) {
                    handler.post(() -> {
                        Toast.makeText(this, "Produto registrado com sucesso!", Toast.LENGTH_LONG).show();
                        finish();
                    });
                } else {
                    handler.post(() -> {
                        Toast.makeText(this, "Erro ao registrar produto", Toast.LENGTH_LONG).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(this,
                        "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}