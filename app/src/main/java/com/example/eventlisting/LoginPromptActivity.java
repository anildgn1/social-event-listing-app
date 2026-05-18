package com.example.eventlisting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginPromptActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_prompt);

        TextView messageText = findViewById(R.id.loginPromptMessage);
        Button loginButton = findViewById(R.id.loginPromptButton);

        messageText.setText("Etkinlik eklemek için lütfen giriş yapın");

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPromptActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Bu ekranı kapat
        });
    }
}
