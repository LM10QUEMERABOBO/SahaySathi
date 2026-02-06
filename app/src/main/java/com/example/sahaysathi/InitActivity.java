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

public class InitActivity extends AppCompatActivity {

    ImageView image;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        image = findViewById(R.id.imageView);
        sharedPreferences = getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);

        // Load logo using Glide (optional)
//        Glide.with(this)
//                .load(R.drawable.app_logo)
//                .into(image);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            String userId = sharedPreferences.getString(ConstantSp.userid, null);

            if (userId == null) {
                startActivity(new Intent(InitActivity.this, Login_Page.class));
            } else {
                Intent intent = new Intent(InitActivity.this, MainActivity2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            finish();

        }, 2000);
    }
}