package com.example.sahaysathi.ui.volunteer.myTasks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sahaysathi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRActivity extends AppCompatActivity {

    ImageView qrImage;
    TextView title, city, instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_qrvolunteer);

        // Initialize views
        qrImage = findViewById(R.id.qrImage);
        title = findViewById(R.id.title);
        city = findViewById(R.id.city);
        instructions = findViewById(R.id.instructions);

        // Get data from intent
        Intent i = getIntent();
        String eventId = i.getStringExtra("eventId");
        String userId = FirebaseAuth.getInstance().getUid();
        String applicationId = i.getStringExtra("applicationId");
        String eventName = i.getStringExtra("eventName");
        String eventCity = i.getStringExtra("city");
        String eventInstructions = i.getStringExtra("instructions");

        // Set UI
        title.setText(eventName != null ? eventName : "N/A");
        city.setText(eventCity != null ? eventCity : "N/A");
        instructions.setText(eventInstructions != null ? eventInstructions : "No Instructions");

       generateQR(applicationId);
    }

    // ✅ QR Generator (Correct way)
    private void generateQR(String text) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();

            Bitmap bitmap = encoder.encodeBitmap(
                    text,
                    BarcodeFormat.QR_CODE,
                    500,
                    500
            );

            qrImage.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}