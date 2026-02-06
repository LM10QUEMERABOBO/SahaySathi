package com.example.sahaysathi;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Forget_Password extends AppCompatActivity {


    Button forget_button,reset_button;

    EditText forget_email,forget_password,forget_confirm_password;

    String email_pattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
    String email;
    SQLiteDatabase db;
    String get_email,get_password,get_confirm_password;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        db = openOrCreateDatabase("volunteer.db", MODE_PRIVATE, null);
        String user_table="CREATE TABLE IF NOT EXISTS volunteer(v_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(50),email VARCHAR(100),contact VARCHAR(15),password VARCHAR(15),image BLOB);";
        db.execSQL(user_table);

        sharedPreferences = getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        forget_button=findViewById(R.id.verify_button);
        reset_button=findViewById(R.id.forget_button);
        forget_email=findViewById(R.id.forget_email);
        forget_password=findViewById(R.id.forget_password);
        forget_confirm_password=findViewById(R.id.forget_confirm_password);

        forget_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_email= forget_email.getText().toString().trim();
                email=forget_email.getText().toString().trim();
                if(get_email.matches(email_pattern)){
                    forget_password.setVisibility(View.VISIBLE);
                    forget_confirm_password.setVisibility(View.VISIBLE);
                    reset_button.setVisibility(View.VISIBLE);
                    forget_button.setVisibility(View.GONE);
                    Toast.makeText(Forget_Password.this, "Valid Email", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Forget_Password.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
            }
            });

        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                get_email= email;
                get_email= forget_email.getText().toString().trim();
                get_password = forget_password.getText().toString().trim();
                get_confirm_password = forget_confirm_password.getText().toString().trim();
                if (get_password.isEmpty()) {
                    forget_password.setError("Password is required");

                }
                else if (get_confirm_password.isEmpty()) {
                    forget_confirm_password.setError("Confirm Password is required");
                }
                else if (!get_password.equals(get_confirm_password)) {
                    forget_confirm_password.setError("Password doesn't match");
                }
                else{
                    String check_user = "SELECT * FROM volunteer WHERE email='" + get_email + "'";
                    if (db.rawQuery(check_user, null).getCount() == 0) {
                    Toast.makeText(Forget_Password.this, "User Doesn't Exists", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
//                        Toast.makeText(Forget_Password.this, "Email verified!", Toast.LENGTH_LONG).show();
                        String update_password = "UPDATE volunteer SET password='" + get_password + "' WHERE email='" + get_email + "'";
                        db.execSQL(update_password);
                        sharedPreferences.edit().putString(ConstantSp.password, get_password).commit();
                        Toast.makeText(Forget_Password.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            }
        });
    }
}