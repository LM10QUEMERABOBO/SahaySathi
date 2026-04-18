package com.example.sahaysathi;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InitActivity extends AppCompatActivity {

    ImageView image;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        image = findViewById(R.id.imageView);
        sharedPreferences = getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
            if (userId == null) {
                startActivity(new Intent(InitActivity.this, Login_Page.class));
                finish();
            } else {
                Intent intent = new Intent(InitActivity.this, MainActivity2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }


        }, 2000);
    }
}