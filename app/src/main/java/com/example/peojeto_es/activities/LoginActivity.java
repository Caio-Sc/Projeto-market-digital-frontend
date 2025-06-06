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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;  // Adicione este import

import com.example.peojeto_es.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LoginActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private Button loginButton;
    private String loginUrl = "http://10.0.2.2:3000/usuarios/login";
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_TOKEN = "user_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Verificar se já existe um token salvo
        if (checkExistingToken()) {
            // Se existir, redirecionar para a MainActivity
            goToMainActivity();
            return;
        }

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Validação básica
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Por favor, preencha todos os campos",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });
    }

    private boolean checkExistingToken() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);
        return token != null;
    }

    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUser(String email, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // Criando a requisição JSON
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("email", email);
                jsonInput.put("senha", password);
                String jsonString = jsonInput.toString();

                // Configurando a conexão HTTP
                URL url = new URL(loginUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                // Enviando dados JSON
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonString.getBytes("UTF-8");
                    os.write(input);
                    os.flush();
                }

                // Lendo a resposta
                int responseCode = connection.getResponseCode();
                StringBuilder response = new StringBuilder();

                // Usa getErrorStream se for código de erro
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

                // Processando a resposta JSON
                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("token")) {
                    String token = jsonResponse.getString("token");

                    // Salva o token e redireciona para a MainActivity
                    handler.post(() -> {
                        saveToken(token);
                        Toast.makeText(LoginActivity.this,
                                "Login realizado com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    });
                } else {
                    String errorMessage = jsonResponse.optString("erro", "Erro desconhecido");
                    handler.post(() -> Toast.makeText(LoginActivity.this,
                            "Erro de login: " + errorMessage,
                            Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                final String errorMessage = e.getMessage();
                handler.post(() -> Toast.makeText(LoginActivity.this,
                        "Erro: " + errorMessage,
                        Toast.LENGTH_LONG).show());
            } finally {
                executor.shutdown();
            }
        });
    }

    // Método para recuperar o token (útil para outras activities)
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    // Método para fazer logout (útil para outras activities)
    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();

        // Redireciona para a LoginActivity
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
