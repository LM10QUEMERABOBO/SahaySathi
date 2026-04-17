package com.example.sahaysathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sahaysathi.AuthRepository;

public class SignUp_Page extends AppCompatActivity {

    EditText signup_name, signup_email, signup_contact,
            signup_password, signup_confirm_password, locationField;

    TextView already_account;
    Button signup_button, volunteerBtn, ngoBtn;
    SharedPreferences sharedPreferences;
    String role = ""; // Firebase-friendly lowercase
    AuthRepository authRepository;

    String email_pattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        authRepository = new AuthRepository();

        // Initialize Views
        signup_name = findViewById(R.id.signup_name);
        signup_email = findViewById(R.id.signup_email);
        signup_contact = findViewById(R.id.signup_contact);
        signup_password = findViewById(R.id.signup_password);
        signup_confirm_password = findViewById(R.id.signup_confirm_password);
        locationField = findViewById(R.id.signup_location);

        volunteerBtn = findViewById(R.id.volunteer_button);
        ngoBtn = findViewById(R.id.ngo_button);
        signup_button = findViewById(R.id.signup_button);
        already_account = findViewById(R.id.already_account);

        // Default
        locationField.setVisibility(View.GONE);

        volunteerBtn.setOnClickListener(v -> {
            role = "volunteer";
            locationField.setVisibility(View.GONE);
            signup_contact.setVisibility(View.VISIBLE);
            volunteerBtn.setBackgroundColor(getResources().getColor(R.color.new_blue));
            ngoBtn.setBackgroundColor(getResources().getColor(R.color.new_red));
            Toast.makeText(this, "Volunteer Selected", Toast.LENGTH_SHORT).show();
        });

        ngoBtn.setOnClickListener(v -> {
            role = "ngo";
            locationField.setVisibility(View.VISIBLE);
            signup_contact.setVisibility(View.GONE);
            ngoBtn.setBackgroundColor(getResources().getColor(R.color.new_blue));
            volunteerBtn.setBackgroundColor(getResources().getColor(R.color.new_red));
            Toast.makeText(this, "NGO Selected", Toast.LENGTH_SHORT).show();
        });

        already_account.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp_Page.this, Login_Page.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

        signup_button.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = signup_name.getText().toString().trim();
        String email = signup_email.getText().toString().trim();
        String contact = signup_contact.getText().toString().trim();
        String password = signup_password.getText().toString().trim();
        String confirmPassword = signup_confirm_password.getText().toString().trim();
        String location = locationField.getText().toString().trim();

        // ✅ Validation (same as your code)
        if (name.isEmpty()) {
            signup_name.setError("Enter Name");
            return;
        }

        if (email.isEmpty()) {
            signup_email.setError("Enter Email");
            return;
        }

        if (!email.matches(email_pattern)) {
            signup_email.setError("Enter Valid Email");
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            signup_password.setError("Minimum 6 Characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            signup_confirm_password.setError("Password Doesn't Match");
            return;
        }

        if (role.equals("volunteer")) {
            if (contact.isEmpty() || contact.length() != 10) {
                signup_contact.setError("Enter Valid 10 Digit Contact");
                return;
            }
        }

        if (role.equals("ngo")) {
            if (location.isEmpty()) {
                locationField.setError("Enter Location");
                return;
            }
        }

        // 🔥 Firebase Signup
        authRepository.registerUser(
                name,
                email,
                password,
                role,
                (success, message) -> {

                    if (success) {

                        Toast.makeText(this, "Signup Successful!", Toast.LENGTH_LONG).show();

                        // Go to Login
                        startActivity(new Intent(SignUp_Page.this, Login_Page.class));
                        finish();

                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                    }
                    return null;
                });
    }
}