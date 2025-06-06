package com.example.peojeto_es.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;           // Adicione esta linha

import com.example.peojeto_es.R;


public class RegisterActivity extends AppCompatActivity {
    private EditText nameField, emailField, passwordField, addressField;
    private Button registerButton;
    private String serverUrl = "http://10.0.2.2:3000/usuarios"; // URL do backend

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        addressField = findViewById(R.id.addressField);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String name = nameField.getText().toString();
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String address = addressField.getText().toString();

            registerUser(name, email, password, address);
        });
    }

    private void registerUser(String name, String email, String password, String address) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("nome", name);
                jsonInput.put("email", email);
                jsonInput.put("senha", password);
                jsonInput.put("endereco", address);
                String jsonString = jsonInput.toString();

                System.out.println("JSON Enviado: " + jsonString);

                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonString.getBytes("UTF-8");
                    os.write(input);
                    os.flush();
                }

                int responseCode = connection.getResponseCode();
                StringBuilder response = new StringBuilder();

                // Lê a resposta do servidor mesmo em caso de erro
                InputStream inputStream = (responseCode >= 200 && responseCode < 300)
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine);
                    }
                }

                // Converte a resposta em JSON para ler a mensagem
                JSONObject jsonResponse = new JSONObject(response.toString());
                String message;

                if (jsonResponse.has("erro")) {
                    // Se tem erro na resposta
                    message = "Erro: " + jsonResponse.getString("erro");
                } else if (jsonResponse.has("mensagem")) {
                    // Se tem mensagem na resposta
                    message = jsonResponse.getString("mensagem");

                    // Verifica se a mensagem indica email inválido
                    if (message.equals("Email invalido")) {
                        message = "O email fornecido é inválido";
                    }
                } else {
                    // Caso não tenha nem erro nem mensagem
                    message = "Resposta inesperada do servidor";
                }

                final String finalMessage = message;
                handler.post(() -> Toast.makeText(RegisterActivity.this, finalMessage, Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                e.printStackTrace();
                final String errorMessage = e.getMessage();
                handler.post(() -> Toast.makeText(RegisterActivity.this, "Erro: " + errorMessage, Toast.LENGTH_LONG).show());
            } finally {
                executor.shutdown();
            }
        });
    }

}
