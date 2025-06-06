package com.example.peojeto_es.activities;

import android.content.Intent;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RegisterStoreActivity extends AppCompatActivity {
    private EditText storeNameField, storeAddressField, storeInfoField;
    private String serverUrl = "http://10.0.2.2:3000/negocio/registrarLoja";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_store);

        storeNameField = findViewById(R.id.storeNameField);
        storeAddressField = findViewById(R.id.storeAddressField);
        storeInfoField = findViewById(R.id.storeInfoField);
        Button registerButton = findViewById(R.id.registerStoreButton);

        registerButton.setOnClickListener(v -> registerStore());
    }

    private void registerStore() {
        String nome = storeNameField.getText().toString().trim();
        String endereco = storeAddressField.getText().toString().trim();
        String info = storeInfoField.getText().toString().trim();

        if (nome.isEmpty() || endereco.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
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
                jsonRequest.put("nome", nome);
                jsonRequest.put("endereco", endereco);
                jsonRequest.put("info", info);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(responseCode >= 400 ? connection.getErrorStream() : connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                final String message = jsonResponse.has("mensagem") ?
                        jsonResponse.getString("mensagem") :
                        jsonResponse.getString("erro");

                handler.post(() -> {
                    Toast.makeText(RegisterStoreActivity.this, message, Toast.LENGTH_LONG).show();
                    if (responseCode == 200 || responseCode == 201) {
                        Intent intent = new Intent(RegisterStoreActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Fecha a activity e volta para a tela anterior
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(RegisterStoreActivity.this,
                        "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}