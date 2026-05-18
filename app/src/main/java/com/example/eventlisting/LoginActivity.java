package com.example.eventlisting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventlisting.database.AppDatabase;
import com.example.eventlisting.database.User;
import com.example.eventlisting.database.UserDao;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            // Room üzerinden kullanıcıyı kontrol et
            UserDao userDao = AppDatabase.getInstance(getApplicationContext()).userDao();
            User user = userDao.getUserByEmailAndPassword(email, password);

            if (user != null) {
                // Giriş başarılı → kullanıcıyı SharedPreferences’a kaydet
                SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                prefs.edit()
                        .putInt("user_id", user.getId())
                        .putString("user_email", user.getEmail())
                        .putString("user_name", user.getFirstName() + " " + user.getLastName())
                        .apply();

                Toast.makeText(this, "Giriş başarılı", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("USER_NAME", user.getFirstName() + " " + user.getLastName());
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Geçersiz e-posta veya şifre", Toast.LENGTH_SHORT).show();
            }
        });

        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }
}
