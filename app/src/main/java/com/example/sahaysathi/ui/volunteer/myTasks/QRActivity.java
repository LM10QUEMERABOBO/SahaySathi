package com.example.sahaysathi.ui.volunteer.myTasks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sahaysathi.R;
import com.example.sahaysathi.ui.volunteer.notifications.ApplicationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRActivity extends AppCompatActivity {

    ImageView qrImage;
    TextView title, city, instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_qrvolunteer);

        qrImage = findViewById(R.id.qrImage);
        title = findViewById(R.id.title);
        city = findViewById(R.id.city);
        instructions = findViewById(R.id.instructions);

        Intent i = getIntent();

        String eventId = i.getStringExtra("eventId");
        String userId = FirebaseAuth.getInstance().getUid();

        title.setText(i.getStringExtra("eventName"));
        city.setText(i.getStringExtra("city"));
        instructions.setText(i.getStringExtra("instructions"));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("applications")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        ApplicationModel model = doc.toObject(ApplicationModel.class);

                        if (model != null) {
                            title.setText(model.getEventName());
                            city.setText(model.getlocation());
                            instructions.setText(model.getInstructions());
                        }
                    }
                });

        generateQR(eventId + "_" + userId);
    }

    private void generateQR(String text) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
