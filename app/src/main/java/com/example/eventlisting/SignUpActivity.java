package com.example.eventlisting;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventlisting.database.AppDatabase;
import com.example.eventlisting.database.User;
import com.example.eventlisting.database.UserDao;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, nameEditText, dobEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.signupEmailEditText);
        passwordEditText = findViewById(R.id.signupPasswordEditText);
        nameEditText = findViewById(R.id.signupNameEditText); // ad ve soyad birlikte girilecek
        dobEditText = findViewById(R.id.signupBirthDateEditText);
        dobEditText.setFocusable(false);
        dobEditText.setOnClickListener(v -> {
            CustomDatePickerDialog dialog = new CustomDatePickerDialog(this, selectedDate -> {
                dobEditText.setText(selectedDate);
            });
            dialog.show();
        });

        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String fullName = nameEditText.getText().toString().trim();
            String dob = dobEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] nameParts = fullName.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            UserDao userDao = AppDatabase.getInstance(getApplicationContext()).userDao();

            // E-posta zaten kayıtlı mı kontrol et (yalnızca e-postaya göre)
            boolean emailExists = false;
            for (User u : userDao.getAllUsers()) {
                if (u.getEmail().equalsIgnoreCase(email)) {
                    emailExists = true;
                    break;
                }
            }

            if (emailExists) {
                Toast.makeText(this, "Bu e-posta ile kayıtlı bir kullanıcı zaten var", Toast.LENGTH_SHORT).show();
            } else {
                User user = new User(email, password, firstName, lastName, dob);
                userDao.insert(user);
                Toast.makeText(this, "Kayıt başarılı, giriş yapabilirsiniz", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
