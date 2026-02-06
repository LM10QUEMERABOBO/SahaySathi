package com.example.sahaysathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sahaysathi.AuthRepository;

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

        forget_password.setOnClickListener(v ->
                startActivity(new Intent(Login_Page.this, Forget_Password.class)));

        already_account.setOnClickListener(v ->
                startActivity(new Intent(Login_Page.this, SignUp_Page.class)));

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

                // Save minimal data (UID)
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(ConstantSp.userid, authRepository.getCurrentUserId());
                editor.apply();

                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(Login_Page.this, MainActivity.class));
                finish();

            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
            return null;
        });
    }
}