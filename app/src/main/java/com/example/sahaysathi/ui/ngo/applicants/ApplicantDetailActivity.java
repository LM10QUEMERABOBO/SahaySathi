package com.example.sahaysathi.ui.ngo.applicants;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sahaysathi.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ApplicantDetailActivity extends AppCompatActivity {

    TextView name, city, skill,email,experience,phone;
    Button btnAccept, btnReject;

    String applicationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_applicant_detail);

        name = findViewById(R.id.detailName);
        city = findViewById(R.id.detailCity);
        skill = findViewById(R.id.detailSkill);
        email = findViewById(R.id.detailEmail);
        phone = findViewById(R.id.detailPhone);
        experience = findViewById(R.id.detailExperience);

        btnAccept = findViewById(R.id.accept_button);
        btnReject = findViewById(R.id.regect_button);

        applicationId = getIntent().getStringExtra("applicationId");

        name.setText(getIntent().getStringExtra("name"));
        city.setText("City: " + getIntent().getStringExtra("city"));
        skill.setText("Skill: " + getIntent().getStringExtra("skill"));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnAccept.setOnClickListener(v -> updateStatus(db, "accepted"));
        btnReject.setOnClickListener(v -> updateStatus(db, "rejected"));
    }

    private void updateStatus(FirebaseFirestore db, String status) {

        if (applicationId == null) {
            Toast.makeText(this, "Invalid application", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("applications")
                .document(applicationId)
                .update("status", status)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}