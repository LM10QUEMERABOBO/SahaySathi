package com.example.sahaysathi;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Forget_Password extends AppCompatActivity {

    EditText forget_email;
    Button verify_button;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        forget_email = findViewById(R.id.forget_email);
        verify_button = findViewById(R.id.verify_button);

        auth = FirebaseAuth.getInstance();

        verify_button.setOnClickListener(v -> sendResetEmail());
    }

    private void sendResetEmail() {

        String email = forget_email.getText().toString().trim();

        if (email.isEmpty()) {
            forget_email.setError("Enter Email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            forget_email.setError("Enter valid email");
            return;
        }

        // 🔥 Firebase Reset Password
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Reset link sent to your email",
                                Toast.LENGTH_LONG).show();

                        finish(); // go back to login
                    } else {
                        Toast.makeText(this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}