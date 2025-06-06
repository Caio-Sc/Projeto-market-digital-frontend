package com.example.peojeto_es.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.peojeto_es.R;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserSettingsActivity extends AppCompatActivity {
    private TextView userName, userEmail, userAddress;
    private LinearLayout regularUserButtons, sellerButtons;
    private String serverUrl = "http://10.0.2.2:3000/usuarios/perfil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        // Inicializar views
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userAddress = findViewById(R.id.userAddress);
        regularUserButtons = findViewById(R.id.regularUserButtons);
        sellerButtons = findViewById(R.id.sellerButtons);

        Button becomeSellerButton = findViewById(R.id.becomeSellerButton);
        Button registerProductButton = findViewById(R.id.registerProductButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Carregar informações do usuário
        loadUserInfo();

        // Configurar listeners dos botões
        becomeSellerButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserSettingsActivity.this, RegisterStoreActivity.class);
            startActivity(intent);
        });

        registerProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserSettingsActivity.this, RegisterProductActivity.class);
            startActivity(intent);
        });

        Button deleteProductButton = findViewById(R.id.deleteProductButton);
        deleteProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserSettingsActivity.this, ManageProductsActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            LoginActivity.logout(this);
        });
    }

    private void loadUserInfo() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Adicionar token de autorização
                String token = LoginActivity.getToken(this);
                connection.setRequestProperty("Authorization", "Bearer " + token);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject usuario = jsonResponse.getJSONObject("usuario");

                handler.post(() -> {
                    try {
                        userName.setText("Nome: " + usuario.getString("nome"));
                        userEmail.setText("Email: " + usuario.getString("email"));
                        userAddress.setText("Endereço: " + usuario.getString("endereco"));

                        // Mostrar/esconder botões baseado no tipo de usuário
                        int tipoUsuario = usuario.getInt("tipo_usuario");
                        regularUserButtons.setVisibility(tipoUsuario == 0 ? View.VISIBLE : View.GONE);
                        sellerButtons.setVisibility(tipoUsuario == 1 ? View.VISIBLE : View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(UserSettingsActivity.this,
                        "Erro ao carregar informações: " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        });
    }

}