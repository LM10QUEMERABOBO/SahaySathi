package com.example.sahaysathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login_Page extends AppCompatActivity {

    EditText login_email, login_password;
    TextView forget_password, already_account;
    Button login_button;

    SharedPreferences sharedPreferences;
    AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        sharedPreferences = getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        authRepository = new AuthRepository();

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        forget_password = findViewById(R.id.forget_password);
        already_account = findViewById(R.id.already_account);
        login_button = findViewById(R.id.login_button);

        forget_password.setOnClickListener(v -> {
            Intent intent = new Intent(Login_Page.this, Forget_Password.class);
            startActivity(intent);
        });

        already_account.setOnClickListener(v -> {
            Intent intent = new Intent(Login_Page.this, SignUp_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        login_button.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email = login_email.getText().toString().trim();
        String password = login_password.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            login_password.setError("Minimum 6 Characters");
            return;
        }

        // 🔥 Firebase Login
        authRepository.loginUser(email, password, (success, message) -> {

            if (success) {

                String userId = authRepository.getCurrentUserId();

                // 🔥 Fetch user data (role, name)
                authRepository.getUserData(userId, userData -> {

                    if (userData != null) {

                        String role = userData.get("role");
                        String name = userData.get("name");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(ConstantSp.email, email);
                        editor.putString(ConstantSp.userid, userId);
                        editor.putString(ConstantSp.role, role);
                        editor.putString(ConstantSp.name, name);
                        editor.apply();

                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login_Page.this, MainActivity2.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }

                    return null;
                });

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }

            return null;
        });
    }
}