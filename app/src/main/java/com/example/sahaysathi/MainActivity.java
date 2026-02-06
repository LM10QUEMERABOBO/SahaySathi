package com.example.sahaysathi;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView textView;

//    SQLiteDatabase db;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        sharedPreferences = getSharedPreferences(ConstantSp.pref, MODE_PRIVATE);
        textView = findViewById(R.id.main_title);
        textView.setText("Welcome "+sharedPreferences.getString(ConstantSp.name, "")+" !");
    }
}