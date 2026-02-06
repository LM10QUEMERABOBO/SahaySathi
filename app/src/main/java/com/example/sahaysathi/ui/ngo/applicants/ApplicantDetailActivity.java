package com.example.sahaysathi.ui.ngo.applicants;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sahaysathi.R;

public class ApplicantDetailActivity extends AppCompatActivity {

    TextView name, city, skill;
    Button btnAccept, btnReject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_applicant_detail);
        name = findViewById(R.id.detailName);
        city = findViewById(R.id.detailCity);
        skill = findViewById(R.id.detailSkill);

        btnAccept = findViewById(R.id.accept_button);
        btnReject = findViewById(R.id.regect_button);

        // Receive data from adapter
        String applicantName = getIntent().getStringExtra("name");
        String applicantCity = getIntent().getStringExtra("city");
        String applicantSkill = getIntent().getStringExtra("skill");

        // Set data to views
        name.setText(applicantName);
        city.setText("City: " + applicantCity);
        skill.setText("Skill: " + applicantSkill);

        // Accept button
        btnAccept.setOnClickListener(v -> {

            Toast.makeText(this,"Volunteer Accepted",Toast.LENGTH_SHORT).show();

            // Here you can update database status = accepted

        });

        // Reject button
        btnReject.setOnClickListener(v -> {

            Toast.makeText(this,"Volunteer Rejected",Toast.LENGTH_SHORT).show();

            // Here you can update database status = rejected

        });
    }
}
